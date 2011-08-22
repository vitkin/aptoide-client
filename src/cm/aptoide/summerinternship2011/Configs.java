/**
 * 
 */
package cm.aptoide.summerinternship2011;

import java.text.SimpleDateFormat;

/**
 * @author rafael
 */
public class Configs {
	
	public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("y-M-d H:m:s");
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String COMMENTS_URL = "http://dev.bazaarandroid.com/webservices/listApkComments/%1$s/%2$s/%3$s/xml";
	public static final String TASTE_URL = "http://dev.bazaarandroid.com/webservices/listApkLikes/%1$s/%2$s/%3$s/xml";;
	
	private Configs(){}
	
}