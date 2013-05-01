package ghostsheep.com.classes;

import ghostsheep.com.emergency.R;
import ghostsheep.com.emergency.setting.Setting;
import ghostsheep.com.emergency.sms.Sms;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class EmergencyView implements OnClickListener {

	private Activity activity;
	private Setting setting = null;
	private String language;
	
	public EmergencyView(Activity activity) {
		this.activity = activity;
		
		initView();
	}
	
	private void initView() {
		language = activity.getResources().getConfiguration().locale.getLanguage();
        
        if (null == setting) {
        	setting = new Setting(activity);
        }
        setting.Load(activity);
	}
	
	/*
     * Button - event 연결
     */
    public void initEvent(View v) {
    	v.findViewById(R.id.btnCall).setOnClickListener(this);
    	v.findViewById(R.id.btnRape).setOnClickListener(this);
    	v.findViewById(R.id.btnViolence).setOnClickListener(this);
    	v.findViewById(R.id.btnKidnap).setOnClickListener(this);
    }
	
    public void callEmergency() {
    	setting.Load(activity);
        if (true == setting.getShortCut()) {
        	if (null == setting.getEmergencyCall() || "" == setting.getEmergencyCall()) {
        		Toast.makeText(activity, activity.getString(R.string.no_emergency_number), Toast.LENGTH_SHORT).show();
        		return ;
        	}
        	
        	makeCall();
        }
    }
    
    /*
     * call Pop-up
     */
    private void makeCall() {
    	setting.Load(activity);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setTitle(activity.getString(R.string.emergency_call));
    	if (0 == language.compareTo("ko")) { // korean check
    		builder.setMessage(setting.getEmergencyCall()
    				+ activity.getString(R.string.do_you_want_to_call_emergency_call) + "?");
    	} else {
    		builder.setMessage(activity.getString(R.string.do_you_want_to_call_emergency_call)
    				+ " " + setting.getEmergencyCall() + "?");
    	}
    	builder.setPositiveButton(activity.getString(R.string.call), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + setting.getEmergencyCall()));
				activity.startActivity(intent);
			}
		});
    	builder.setNegativeButton(activity.getString(R.string.cancel), null);
		builder.show();
    }
    
    /*
     * message send
     */
    private void makeSMS(View v) {
    	setting.Load(activity);
    	
    	Sms sms = new Sms(activity, setting, v.getId());
    	sms.sendMessage();
    }
    
    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	switch(v.getId()) {
    	case R.id.btnCall:
    		makeCall();
    		break;
    	case R.id.btnRape:
    		makeSMS(v);
    		break;
    	case R.id.btnViolence:
    		makeSMS(v);
    		break;
    	case R.id.btnKidnap:
    		makeSMS(v);
    		break;
    	}
    }
    
    
}
