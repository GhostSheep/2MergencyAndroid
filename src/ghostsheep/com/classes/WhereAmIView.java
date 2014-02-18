package ghostsheep.com.classes;

import ghostsheep.com.emergency.setting.Setting;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class WhereAmIView implements OnClickListener {
	private Context context;
	
	private Setting setting = null;
	
	public WhereAmIView(Context context) {
		this.context = context;
	}
	
	public void initView(View view) {
		if (null == setting) {
        	setting = new Setting(context);
        }
        setting.Load(context);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		}
	}
    
    public void initEvent() {
		
    }
    
}
