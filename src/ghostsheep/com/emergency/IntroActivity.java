package ghostsheep.com.emergency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity {
	private Handler m_Handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	// delay & move to next activity
     	m_Handler.postDelayed(new Runnable() {
    		@Override
     		public void run() {
    			moveMainActivity();
     		}
     	}, 1000);
    }
    
    private void moveMainActivity() {
    	Intent intent = new Intent(this, MainActivity.class);
        
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
    	
    	if (m_Handler != null) {
    		m_Handler = null;
    	}
    	super.onDestroy();
    }
}
