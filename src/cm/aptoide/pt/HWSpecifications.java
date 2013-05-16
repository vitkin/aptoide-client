package cm.aptoide.pt;


import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class HWSpecifications{


	public int sdkVer;
	public int screenSize;
	public String esglVer;


	public HWSpecifications(Context context) {
		// TODO Auto-generated constructor stub
		//	switch (screenSizeInt) {
		//	case 1:
		//		this.screenSize="smallScreen";
		//		break;
		//	case 2:
		//		this.screenSize="normalScreen";
		//	break;
		//	
		//	case 3:
		//		this.screenSize="largeScreen";
		//		break;
		//
		//	default:
		//		this.screenSize="Undefined Screen Size";
		//		break;
		//	}

	}



	/**
	 * @return the sdkVer
	 */

	static public int getSdkVer() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * @return the screenSize
	 */
	static public int getScreenSize(Context context) {
		return context.getResources().getConfiguration().screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
	}

	/**
	 * @return the esglVer
	 */
	static public String getEsglVer(Context context) {
		return ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
	}


}
