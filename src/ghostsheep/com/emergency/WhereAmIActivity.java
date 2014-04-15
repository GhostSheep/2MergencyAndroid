package ghostsheep.com.emergency;

import ghostsheep.com.emergency.setting.Setting;
import ghostsheep.com.emergency.sms.Sms;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class WhereAmIActivity extends FragmentActivity
implements OnInfoWindowClickListener, OnMarkerDragListener, SensorEventListener, LocationListener {
	
	private Sms sms;
	private Setting setting = null;
	GoogleMap googleMap;
	
	private String locationInfo;
	
	// 진동 확인을 위한 변수
	private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
   
    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 800;
    private int cnt;
    
	private SensorManager sensorManager;
    private Sensor accelerormeterSensor;
    
    // siren 변수
    private SoundPool soundPool;
    private int siren;
    private int streamID;
    private AudioManager audioManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_am_i);
        
        if (null == setting) {
        	setting = new Setting(getApplicationContext());
        }
        setting.Load(getApplicationContext());
        
        // map setting
        initLocation();
        
        // 사이렌 setting
        initSound();
    }
    
    private void initLocation()
    {
    	SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.whereAmI);
        googleMap = supportMapFragment.getMap();
        
        googleMap.setMyLocationEnabled(true);
        
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
        // GPS, NETWORK로부터 위치 정보 갱신 요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        
        String provider = locationManager.getBestProvider(new Criteria(), true);
        
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
        	double latitude = location.getLatitude();
        	double longitude = location.getLongitude();
        	
        	LatLng latLng = new LatLng(latitude, longitude);
        	getAddress(latLng);
        	
        	googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        	googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        	
        	googleMap.addMarker(new MarkerOptions().position(latLng).title(locationInfo).draggable(true));
        	googleMap.setOnMarkerDragListener(this);
        	googleMap.setOnInfoWindowClickListener(this);
        	
        	Toast.makeText(getApplicationContext(), getString(R.string.fixMarker_N_SendSms), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initSound() {
    	cnt = 0;
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
    	soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
    	siren = soundPool.load( getApplicationContext(), R.raw.siren, 1 );
    }
    
    @Override
    protected void onStart() {
    	
    	if (accelerormeterSensor != null) {
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
    	}
    	
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	if (sensorManager != null) {
            sensorManager.unregisterListener(this);
    	}
    	
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    @Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
    	String location = marker.getTitle();
    	if (location.compareTo("Here!") == 0) {
    		return;
    	}
    	
    	final String message = getString(R.string.violence_message) + location;
    	
    	AlertDialog.Builder alert = new Builder(WhereAmIActivity.this);
		alert.setMessage(getString(R.string.send_Sms));
		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				sms = new Sms(getApplicationContext(), setting);
				sms.sendMessage(message);
			}
		});
		alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alert.show();
	}
    
    @Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		LatLng nowPosition = marker.getPosition();
		getAddress(nowPosition);
		marker.setTitle(this.locationInfo);
	}
	
	/*
     * 현재 지역을 기반으로 주소를 뽑아냄
     */
    private void getAddress(LatLng nowPosition) {
    	try {
    		String locationInfo = "";
			Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
	        List<Address> addresses = geo.getFromLocation(nowPosition.latitude, nowPosition.longitude, 1);
	        if (addresses.isEmpty()) {
	        	locationInfo = "Waiting for Location";
	        } else {
	            if (addresses.size() > 0) {
	            	locationInfo = addresses.get(0).getAddressLine(0);
	            }
	        }
	        
	        this.locationInfo = locationInfo;
	    }
	    catch (Exception e) {
	        e.printStackTrace(); // getFromLocation() may sometimes fail
	    }
    }

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	// TODO Auto-generated method stub
    	
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    	// TODO Auto-generated method stub
    	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            if (gabOfTime > 150) {
                lastTime = currentTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) /
                        gabOfTime * 10000;

                // 흔들림 감지 시
                if (speed > SHAKE_THRESHOLD) {
                    cnt++;
                    if (cnt > 4) {
                    	if (0 >= streamID) {
                    		if (null == audioManager) {
                    			audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                    		}
                    		final int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    		int volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMax, AudioManager.FLAG_PLAY_SOUND);
	                    	streamID = soundPool.play( siren, 1f, 1f, 0, -1, 1f );
	                    	cnt = 0;
	                    	
	                    	AlertDialog.Builder builder = new AlertDialog.Builder(WhereAmIActivity.this);
	                    	builder.setTitle(getString(R.string.turn_off_a_sound));
	                    	builder.setNegativeButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									soundPool.stop(streamID);
									streamID = 0;
									audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
								}
							});
	                    	builder.setCancelable(false);
                		builder.show();
                    	}
                    }
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
