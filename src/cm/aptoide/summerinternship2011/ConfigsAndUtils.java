/**
 * 
 */
package cm.aptoide.summerinternship2011;

import java.text.SimpleDateFormat;

/**
 * @author rafael
 */
public class ConfigsAndUtils {
	
	public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("y-M-d H:m:s");
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String COMMENTS_URL_LIST = "http://dev.bazaarandroid.com/webservices/listApkComments/%1$s/%2$s/%3$s/xml";
	public static final String TASTE_URL_LIST = "http://dev.bazaarandroid.com/webservices/listApkLikes/%1$s/%2$s/%3$s/xml";;
	public static final String COMMENTS_URL_ADD = "http://dev.bazaarandroid.com/webservices/addApkComment";
	public static final String TASTE_URL_ADD = "http://dev.bazaarandroid.com/webservices/addApkLike";
	public static final String QR_CODE_GOOGLE_WEBSERVICE = "https://chart.googleapis.com/chart";
	
	private ConfigsAndUtils(){}
	
	public static String byteArrayToHexString(byte[] b) {
		  StringBuilder result = new StringBuilder("");
		  for (int i=0; i < b.length; i++) {
		    result.append(Integer.toString( (b[i] & 0xff) + 0x100 , 16).substring( 1 ));
		  }
		  return result.toString();
	}
	
}