package cm.aptoide.pt;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.os.Build;

public class HWSpecifications{


public int sdkVer;
public String screenSize;
public String esglVer;


public HWSpecifications(Context context) {
	// TODO Auto-generated constructor stub
	this.sdkVer= Build.VERSION.SDK_INT;
	
	
	int screenSizeInt=context.getResources().getConfiguration().screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
	
	switch (screenSizeInt) {
	case 1:
		this.screenSize="smallScreen";
		break;
	case 2:
		this.screenSize="normalScreen";
	break;
	
	case 3:
		this.screenSize="largeScreen";
		break;

	default:
		this.screenSize="Undefined Screen Size";
		break;
	}
	
	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
	this.esglVer=configInfo.getGlEsVersion();
}



/**
 * @return the sdkVer
 */

public int getSdkVer() {
	return sdkVer;
}

/**
 * @return the screenSize
 */
public String getScreenSize() {
	return screenSize;
}

/**
 * @return the esglVer
 */
public String getEsglVer() {
	return esglVer;
}


}
