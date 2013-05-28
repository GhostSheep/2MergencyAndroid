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
import android.location.LocationManager;
import android.net.Uri;
import android.widget.Toast;

public class Sms {
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
	
	public void sendMessage() {
		
		// 지역 확인을 위한...
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
        String provider = locationManager.getBestProvider(new Criteria(), true);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    getAddress(location);
	    
	    String message = makeMessage(provider);
	    
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
    private String makeMessage(String provider) {
    	String message;

    	if (null == locationInfo) {
    		locationInfo = "";
    	}
    	
    	if (0 != provider.compareTo("gps")){
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
    	sendIntent.putExtra("address", setting.getEmergencySms());
    	sendIntent.putExtra("sms_body", message);
    	context.startActivity(sendIntent);
    }
}
