package ghostsheep.com.emergency.setting;

import ghostsheep.com.emergency.R;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("serial")
public class Setting implements Serializable {

	private String pushTarget;
	private Boolean shortCut;
	private String emergencyCall;
	private String emergencySms;
	private String addedNumber1;
	private String addedNumber2;
	private String addedNumber3;
	private String addedNumber4;
	private String addedNumber5;
	
	public Setting(Context context) {
		pushTarget = "";
		shortCut = false;
		emergencyCall = context.getString(R.string.emergency_number);
		emergencySms = context.getString(R.string.emergency_number);
		addedNumber1 = "";
		addedNumber2 = "";
		addedNumber3 = "";
		addedNumber4 = "";
		addedNumber5 = "";
	}
	
	public String getPushTarget() {
		return pushTarget;
	}
	
	public void setPushTarget(String pushTarget) {
		this.pushTarget = pushTarget;
	}
	
	public Boolean getShortCut() {
		return shortCut;
	}
	
	public void setShortCut(Boolean shortCut) {
		this.shortCut = shortCut;
	}
	
	public String getEmergencyCall() {
		return emergencyCall;
	}
	
	public void setEmergencyCall(String emergencyCall) {
		this.emergencyCall = emergencyCall.replaceAll("\\p{Punct}", "");
	}
	
	public String getEmergencySms() {
		return emergencySms;
	}
	
	public void setEmergencySms(String emergencySms) {
		this.emergencySms = emergencySms.replaceAll("\\p{Punct}", "");
	}

	public String getAddedNumber(int number) {
		if (1 == number) {
			return addedNumber1;
		} else if (2 == number) {
			return addedNumber2;
		} else if (3 == number) {
			return addedNumber3;
		} else if (4 == number) {
			return addedNumber4;
		} else {  // 5 == number
			return addedNumber5;
		}
	}

	public void setAddedNumber(String addedNumber, int number) {
		if (1 == number) {
			this.addedNumber1 = addedNumber.replaceAll("\\p{Punct}", "");
		} else if (2 == number) {
			this.addedNumber2 = addedNumber.replaceAll("\\p{Punct}", "");
		} else if (3 == number) {
			this.addedNumber3 = addedNumber.replaceAll("\\p{Punct}", "");
		} else if (4 == number) {
			this.addedNumber4 = addedNumber.replaceAll("\\p{Punct}", "");
		} else {  // 5 == number
			this.addedNumber5 = addedNumber.replaceAll("\\p{Punct}", "");
		}
	}

	/*
	 * Setting info save
	 */
	@SuppressWarnings("static-access")
	public void Save(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("Pref", context.MODE_PRIVATE);
		SharedPreferences.Editor prefEdit = preferences.edit();
		
		prefEdit.putString("pushTarget", getPushTarget());
		prefEdit.putBoolean("shortCut", getShortCut());
		prefEdit.putString("emergencyCall", getEmergencyCall());
		prefEdit.putString("emergencySms", getEmergencySms());
		prefEdit.putString("addedNumber1", getAddedNumber(1));
		prefEdit.putString("addedNumber2", getAddedNumber(2));
		prefEdit.putString("addedNumber3", getAddedNumber(3));
		prefEdit.putString("addedNumber4", getAddedNumber(4));
		prefEdit.putString("addedNumber5", getAddedNumber(5));
		
		prefEdit.commit();
	}
	
	/*
	 * Setting info load
	 */
	@SuppressWarnings("static-access")
	public void Load(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("Pref", context.MODE_PRIVATE);
		
		setPushTarget(preferences.getString("pushTarget", pushTarget));
		setShortCut(preferences.getBoolean("shortCut", shortCut));
		setEmergencyCall(preferences.getString("emergencyCall", emergencyCall));
		setEmergencySms(preferences.getString("emergencySms", emergencySms));
		setAddedNumber(preferences.getString("addedNumber1", addedNumber1), 1);
		setAddedNumber(preferences.getString("addedNumber2", addedNumber2), 2);
		setAddedNumber(preferences.getString("addedNumber3", addedNumber3), 3);
		setAddedNumber(preferences.getString("addedNumber4", addedNumber4), 4);
		setAddedNumber(preferences.getString("addedNumber5", addedNumber5), 5);
	}
}
