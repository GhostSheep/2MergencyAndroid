package ghostsheep.com.classes;

import ghostsheep.com.emergency.R;
import ghostsheep.com.emergency.setting.Setting;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class SettingView implements OnClickListener {
	private Context context;
	private View view;
	
	public static final int PICK_CONTACT = 1;
	
	private int controlNum;
	private Setting setting;
	
	private boolean bShortCut;
	
	private String buttonName;
	
	private EditText emergencyCall;
	private CheckBox phoneNumberCheck;
	private EditText emergencySms;
	private ImageView shortCut;
	private LinearLayout numbers;
	
	public SettingView(Context context) {
		this.context = context;
	}
	
    public void setResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    		case PICK_CONTACT:
    			if (Activity.RESULT_OK == resultCode) {
    				Uri contactData = data.getData();
    				contactPick(contactData);
    			}
    	}
    }
	
    /*
     * View init
     */
    public void initView(View view) {
    	if (null == setting) {
        	setting = new Setting(context);
        }
        setting.Load(context);
        
        this.view = view;
        
        controlNum = 1;
        
    	emergencyCall = (EditText)view.findViewById(R.id.emergencyCall);
    	phoneNumberCheck = (CheckBox)view.findViewById(R.id.phoneNumberCheck);
    	emergencySms = (EditText)view.findViewById(R.id.emergencySms);
    	shortCut = (ImageView)view.findViewById(R.id.shortCut);
    	numbers = (LinearLayout)view.findViewById(R.id.numbers);
    	
    	emergencyCall.setText(setting.getEmergencyCall());
    	emergencyCall.setBackgroundColor(Color.TRANSPARENT);
    	emergencySms.setText(setting.getEmergencySms());
    	emergencySms.setBackgroundColor(Color.TRANSPARENT);
    	
    	if (0 == setting.getEmergencyCall().compareTo(setting.getEmergencySms())) {
    		phoneNumberCheck.setChecked(true);
    		emergencySms.setEnabled(false);
    	} else {
    		phoneNumberCheck.setChecked(false);
    		emergencySms.setEnabled(true);
    	}
    	
    	bShortCut = setting.getShortCut();
    	if (true == bShortCut) {
    		shortCut.setImageResource(R.drawable.toggle_clicked);
    	} else {
    		shortCut.setImageResource(R.drawable.toggle);
    	}
    	
    	EditText tempEdit;
		ArrayList<String> numbers = new ArrayList<String>();
		for (int i = 1; i < 6; ++i) {
			if (0 != setting.getAddedNumber(i).compareTo("")) {
				numbers.add(setting.getAddedNumber(i));
			}
		}
		
		for (int i = 0; i < numbers.size(); ++i) {
			addNumber();
			tempEdit = (EditText)view.findViewById(Integer.valueOf(i + 1));
			tempEdit.setText(numbers.get(i));
		}
    }
    
    /*
     * Event connection
     */
    public void initEvent(View view) {
    	view.findViewById(R.id.searchCall).setOnClickListener(this);
    	view.findViewById(R.id.searchSms).setOnClickListener(this);
    	view.findViewById(R.id.addNumber).setOnClickListener(this);
    	shortCut.setOnClickListener(this);
    	
    	emergencyCall.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (true == phoneNumberCheck.isChecked()) {
					emergencySms.setText(emergencyCall.getText().toString());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	// check box
    	phoneNumberCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (true == isChecked) {
					emergencySms.setEnabled(false);
					emergencySms.setText(emergencyCall.getText());
				}
				else {
					emergencySms.setEnabled(true);
					emergencySms.setText("");
				}
			}
		});
    }
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
    	switch(v.getId()) {
    	case R.id.searchCall:
    		buttonName = "searchCall";
			getNumber();
			break;
    	case R.id.searchSms:
    		if (false == phoneNumberCheck.isChecked()) {
				buttonName = "searchSms";
				getNumber();
			}
    		break;
    	case R.id.shortCut:
    		if (true == bShortCut) {
				bShortCut = false;
				shortCut.setImageResource(R.drawable.toggle);
	    	} else {
	    		bShortCut = true;
	    		shortCut.setImageResource(R.drawable.toggle_clicked);
	    	}
    		break;
    	case R.id.addNumber:
    		addNumber();
    		break;
    	default:
    		buttonName = String.valueOf(v.getId());
    		getNumber();
    		break;
    	}
	}
    
    
    /*
     * 설정 저장
     */
    public void SaveSetting() {
    	if(null == setting) {
    		setting = new Setting(context);
    	}
    	
    	setting.setEmergencyCall(emergencyCall.getText().toString());
    	setting.setEmergencySms(emergencySms.getText().toString());
    	setting.setPushTarget("");
    	setting.setShortCut(bShortCut);
    	
    	if (1 < controlNum) {
    		EditText tempEdit;
    		for (int i = 1; i < controlNum; ++i) {
    			tempEdit = (EditText)view.findViewById(Integer.valueOf(i));
    			setting.setAddedNumber(tempEdit.getText().toString(), i);
    		}
    	}
    	
    	setting.Save(context);
    }
    
    private void getNumber() {
    	Intent contact_picker = new Intent(Intent.ACTION_PICK);
    	contact_picker.setType(ContactsContract.Contacts.CONTENT_TYPE);
    	((Activity)context).startActivityForResult(contact_picker, PICK_CONTACT);
    }
    
    private void contactPick(Uri dataUri) {
    	String number = null;
    	Cursor cursor = context.getContentResolver().query(dataUri, null, null, null, null);
    	
		while(cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			
			if (true == hasPhoneNumber.equalsIgnoreCase("1")) {
				
				Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
				String mobile = "";
				String other = "";
				while (phone.moveToNext()) {
					if (Phone.TYPE_MOBILE == phone.getInt(phone.getColumnIndex(Phone.TYPE))) {
						mobile = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					} else {
						other = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
				}
				
				if (null == mobile || 0 == mobile.compareTo("")) {
					number = other.replaceAll("\\p{Punct}", "");
				} else {
					number = mobile.replaceAll("\\p{Punct}", "");
				}
				
				phone.close();
			}
			
			if (0 == buttonName.compareTo("searchSms")) {
				emergencySms.setText(number);
			} else if (0 == buttonName.compareTo("searchCall")){
				emergencyCall.setText(number);
			} else {
				EditText addedNumer = (EditText)view.findViewById(Integer.valueOf(buttonName) - 100);
				addedNumer.setText(number);
			}
			break;
		}
    }
    
    /*
     * 번호 추가
     */
    private void addNumber() {
    	if (5 < controlNum) {
    		Toast.makeText(context, "추가 번호는 최대 5개까지만 저장할 수 있습니다.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	LinearLayout layout = new LinearLayout(context);
    	LinearLayout.LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	lParams.height = PixelfromDP(54);
    	lParams.topMargin = PixelfromDP(5);
    	layout.setLayoutParams(lParams);
    	layout.setBackgroundColor(Color.parseColor("#696969"));
    	layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
    	
    	TextView text = new TextView(context);
    	LinearLayout.LayoutParams tParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    	tParams.weight = 2;
    	tParams.leftMargin = PixelfromDP(10);
    	tParams.topMargin = PixelfromDP(13);
    	text.setLayoutParams(tParams);
    	text.setTextAppearance(context, R.style.textViewStyle);
    	text.setText(context.getString(R.string.added_number));
    	layout.addView(text);
    	
    	EditText edit = new EditText(context);
    	LinearLayout.LayoutParams eParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    	eParams.weight = 1;
    	edit.setLayoutParams(eParams);
    	edit.setInputType(InputType.TYPE_CLASS_PHONE);
    	edit.setSingleLine();
    	edit.setTextColor(Color.parseColor("#FFFFFF"));
    	edit.setBackgroundColor(Color.TRANSPARENT);
    	edit.setHint(R.string.input_number);
    	edit.setId(controlNum);
    	layout.addView(edit);
    	
    	ImageView image = new ImageView(context);
    	LinearLayout.LayoutParams iParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    	iParams.rightMargin = PixelfromDP(10);
    	iParams.topMargin = PixelfromDP(15);
    	image.setLayoutParams(iParams);
    	image.setImageResource(R.drawable.search);
    	image.setId(100 + controlNum);
    	image.setOnClickListener(this);
    	layout.addView(image);
    	
    	LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	params.topMargin = PixelfromDP(5);
    	numbers.addView(layout, params);
    	
    	++controlNum;
    }
    
    private int PixelfromDP(int DP) {
    	return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
    }
}
