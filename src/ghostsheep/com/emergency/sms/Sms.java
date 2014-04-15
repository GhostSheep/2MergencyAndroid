package ghostsheep.com.emergency.sms;

import ghostsheep.com.emergency.R;
import ghostsheep.com.emergency.setting.Setting;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class Sms implements LocationListener {
	private Context context;
	private Setting setting;
	private int viewId;
	
	private LocationManager locationManager;
	private String locationInfo;
	
	public Sms(Context context, Setting setting, int viewID) {
		this.context = context;
		this.setting = setting;
		this.viewId = viewID;
	}
	
	public Sms(Context context, Setting setting) {
		this.context = context;
		this.setting = setting;
	}
	
	public void sendMessage() {
		// 지역 확인을 위한...
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
        // GPS, NETWORK로부터 위치 정보 갱신 요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        
        String provider = locationManager.getBestProvider(new Criteria(), true);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    // GSP 사용 확인
	    boolean isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    
	    getAddress(location);
	    
	    String message = makeMessage(isGps);
	    
	    sendSMS(message);
	}
	
	public void sendMessage(String message) {
	    
	    sendSMS(message);
	}
	
	/*
     * 현재 지역을 기반으로 주소를 뽑아냄
     */
    private void getAddress(Location location) {
    	try {
    		String locationInfo = "";
    		
    		// GPS check
			if (location == null) {
				Toast.makeText(context, context.getString(R.string.enable_location_service), Toast.LENGTH_SHORT).show();
				return ;
			}
			
			// 주소를 가져오기 위한 설정
			Geocoder geo = new Geocoder(context, Locale.getDefault());
	        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
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
    
    /*
     * 상황별 주소 내용을 포함한 message 생성
     */
    private String makeMessage(Boolean isGPS) {
    	String message;

    	if (null == locationInfo) {
    		locationInfo = "";
    	}
    	
    	if (isGPS == false){
    		locationInfo += "/" + context.getString(R.string.not_gps_provider);
    	}
    	if (R.id.btnRape == viewId) { // Rape
    		message = context.getString(R.string.rape_message) + "/" + locationInfo;
    	} else if (R.id.btnViolence == viewId) {  // Violence
    		message = context.getString(R.string.violence_message) + "/" + locationInfo;
    	} else if (R.id.btnKidnap ==viewId) {
    		message = context.getString(R.string.kidnap_message) + "/" + locationInfo;
    	} else {  // Kidnap
    		message = context.getString(R.string.violence_message) + "/" + locationInfo;
    	}
    	
    	return message;
    }
    
    /*
     * 지정된 번호로 만들어진 message 전송
     */
    private void sendSMS(String message) {
    	String url = setting.getEmergencySms();
    	
    	for (int i = 1; i < 6; ++i) {
    		if (0 != setting.getAddedNumber(i).compareTo("")) {
    			url = url + ";" + setting.getAddedNumber(i);
    		}
    	}
    	Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + url));
    	sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	sendIntent.putExtra("address", setting.getEmergencySms());
    	sendIntent.putExtra("sms_body", message);
    	context.startActivity(sendIntent);
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
