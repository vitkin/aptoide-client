package cm.aptoide.pt;

import java.text.SimpleDateFormat;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class Configs {
	 
	public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("d-M-y H:m:s");
	
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final String WEB_SERVICE_COMMENTS_LIST 			= "http://www.bazaarandroid.com/webservices/listApkComments/%1$s/%2$s/%3$s/xml"; // GET
	public static final String WEB_SERVICE_TASTE_LIST 				= "http://www.bazaarandroid.com/webservices/listApkLikes/%1$s/%2$s/%3$s/xml"; // GET
	public static final String WEB_SERVICE_POST_COMMENT_ADD			= "http://www.bazaarandroid.com/webservices/addApkComment"; // POST
	public static final String WEB_SERVICE_GET_TASTE_ADD			= "http://www.bazaarandroid.com/webservices/addApkLike/user/%1$s/%2$s/%3$s/%4$s/%5$s/%6$s"; // GET
	public static final String WEB_SERVICE_GET_CHECK_CREDENTIALS	= "http://www.bazaarandroid.com/webservices/checkUserCredentials/%1s/%2s/xml"; // GET
	public static final String WEB_SERVICE_SCREENS_LIST 			= "http://www.bazaarandroid.com/webservices/listApkScreens/";
	
	public static final String LOGIN_USER_ID 	= "useridLogin";
	public static final String LOGIN_PASSWORD 	= "passwordLogin";
	public static final String LOGIN_USER_NAME 	= "usernameLogin";
	
	public static final boolean INTERFACE_SILVER_TABS_ON	= false;
	public static final boolean INTERFACE_TABS_ON_BOTTOM 	= false;
	public static final boolean BACKGROUND_ON_TABS 			= false;
	
	public static final String TERMINAL_INFO = android.os.Build.MODEL + "("+ android.os.Build.PRODUCT + ")"
	+";v"+android.os.Build.VERSION.RELEASE+";"+System.getProperty("os.arch");
	
	/**
	 * The minimum amount of items to have below your current scroll position, before loading more.
	 */
	public final static int VISIBLE_THRESHOLD_COMMENTS = 4; 
	/**
	 * The number of comments to retrieve per fetch
	 */
	public final static int COMMNETS_TO_LOAD = 4;
	
	public final static boolean TASTE_ON = true; 
	public final static boolean COMMENTS_ON = true;
	public final static boolean COMMENTS_ADD_ON = true; // For this option have any meaning COMMENTS_LIST_ON = true;
	public final static boolean TASTE_ADD_ON = true; 	// For this option have any meaning TASTE_LIST_ON = true;
	
	
	private Configs(){}
	
}