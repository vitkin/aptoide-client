package cm.aptoide.pt;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

public class HWSpecifications extends Activity{
/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.catglist);
		
	}

private int sdkVer = Build.VERSION.SDK_INT;

private int screenSize;
private String esglVer;


//public HWSpecifications(Context context) {
//	// TODO Auto-generated constructor stub
//	this.screenSize=context.getResources().getConfiguration().screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
//	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
//	
//	this.esglVer=configInfo.getGlEsVersion();
//}

/**
 * @return the sdkVer
 */

public int getSdkVer() {
	return sdkVer;
}

/**
 * @return the screenSize
 */
public int getScreenSize() {
	return screenSize;
}

/**
 * @return the esglVer
 */
public String getEsglVer() {
	return esglVer;
}


}
