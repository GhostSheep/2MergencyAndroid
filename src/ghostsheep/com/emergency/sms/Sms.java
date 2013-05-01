package ghostsheep.com.emergency.sms;

import ghostsheep.com.emergency.R;
import ghostsheep.com.emergency.setting.Setting;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.widget.Toast;

public class Sms {
	private Activity act;
	private Setting setting;
	private int viewId;
	
	private LocationManager locationManager;
	private String locationInfo;
	
	public Sms(Activity act, Setting setting, int viewID) {
		this.act = act;
		this.setting = setting;
		this.viewId = viewID;
	}
	
	public void sendMessage() {
		
		// 현재 위치 주소를 얻기 위한 사전 준비
        locationManager = (LocationManager)act.getSystemService(Context.LOCATION_SERVICE);
        
        String provider = locationManager.getBestProvider(new Criteria(), true);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    getAddress(location);
	    
	    String message = makeMessage(provider);
	    
	    sendSMS(message);
	}
	
	/*
     * 현재 Location 정보를 기반으로 주소를 얻어옴
     */
    private void getAddress(Location location) {
    	try {
    		String locationInfo = "";
    		
    		// GPS 확인
			if (location == null) {
				Toast.makeText(act, act.getString(R.string.enable_location_service), Toast.LENGTH_SHORT).show();
				return ;
			}
			
			Geocoder geo = new Geocoder(act.getApplicationContext(), Locale.getDefault());
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
     * 상황별 위치와 상태를 합쳐서 Message 제작
     */
    private String makeMessage(String provider) {
    	String message;

    	if (null == locationInfo) {
    		locationInfo = "";
    	}
    	
    	if (0 != provider.compareTo("gps")){
    		locationInfo += "/" + act.getString(R.string.not_gps_provider);
    	}
    	if (R.id.btnRape == viewId) { // Rape
    		message = act.getString(R.string.rape_message) + "/" + locationInfo;
    	} else if (R.id.btnViolence == viewId) {  // Violence
    		message = act.getString(R.string.violence_message) + "/" + locationInfo;
    	} else if (R.id.btnKidnap ==viewId) {
    		message = act.getString(R.string.kidnap_message) + "/" + locationInfo;
    	} else {  // Kidnap
    		message = act.getString(R.string.violence_message) + "/" + locationInfo;
    	}
    	
    	return message;
    }
    
    /*
     * 제작된 Message 를 SMS로 전송
     */
    private void sendSMS(String message) {
    	String url = setting.getEmergencySms();
    	
    	for (int i = 1; i < 6; ++i) {
    		if (0 != setting.getAddedNumber(i).compareTo("")) {
    			url = url + ";" + setting.getAddedNumber(i);
    		}
    	}
    	Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + url));
    	sendIntent.putExtra("address", setting.getEmergencySms());
    	sendIntent.putExtra("sms_body", message);
    	act.startActivity(sendIntent);
    }
}
