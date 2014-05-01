package ghostsheep.com.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class RecycleUtils {

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void recursiveRecycle(View root) {

        if (root == null)

            return;


        if (Build.VERSION.SDK_INT >= 16) {
        	root.setBackground(null);
        } else {
        	root.setBackgroundDrawable(null);
        }

        if (root instanceof ViewGroup) {

            ViewGroup group = (ViewGroup)root;

            int count = group.getChildCount();

            for (int i = 0; i < count; i++) {

                recursiveRecycle(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView)) {

                group.removeAllViews();
            }
        }

        if (root instanceof ImageView) {

            ((ImageView)root).setImageDrawable(null);

        }
        
        if (root instanceof RadioGroup) {
        	((RadioGroup) root).setOnCheckedChangeListener(null);
        }
        
        if (root instanceof CheckBox) {
        	((CheckBox) root).setOnCheckedChangeListener(null);
        }
        
        if (!(root instanceof AdapterView)) {

        	root.setOnClickListener(null);
        }
        root = null;
    }
}
