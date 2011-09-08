/**
 * 
 */
package cm.aptoide.summerinternship2011;

import java.text.SimpleDateFormat;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class ConfigsAndUtils {
	 
	public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("d-M-y H:m:s");
	
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static final String WEB_SERVICE_COMMENTS_LIST = "http://www.bazaarandroid.com/webservices/listApkComments/%1$s/%2$s/%3$s/xml";
	public static final String WEB_SERVICE_TASTE_LIST = "http://www.bazaarandroid.com/webservices/listApkLikes/%1$s/%2$s/%3$s/xml";
	public static final String WEB_SERVICE_POST_COMMENT_ADD = "http://www.bazaarandroid.com/webservices/addApkComment";
	public static final String WEB_SERVICE_GET_TASTE_ADD = "http://www.bazaarandroid.com/webservices/addApkLike/user/%1$s/%2$s/%3$s/%4$s/%5$s/%6$s";
	public static final String WEB_SERVICE_GET_CHECK_CREDENTIALS = "http://www.bazaarandroid.com/webservices/checkUserCredentials/%1s/%2s/xml";
	
	public static final String LOGIN_USER_ID = "useridLogin";
	public static final String LOGIN_PASSWORD = "passwordLogin";
	public static final String LOGIN_USER_NAME = "usernameLogin";
	
	//public static final String QR_CODE_GOOGLE_WEBSERVICE = "https://chart.googleapis.com/chart";
	
	private ConfigsAndUtils(){}
	
	/**
	 * Useful to convert the digest to the hash
	 * 
	 * @param b
	 * @return
	 */
	public static String byteArrayToHexString(byte[] b) {
		  StringBuilder result = new StringBuilder("");
		  for (int i=0; i < b.length; i++) {
		    result.append(Integer.toString( (b[i] & 0xff) + 0x100 , 16).substring( 1 ));
		  }
		  return result.toString();
	}
	
}