/**
 * 
 */
package cm.aptoide.summerinternship2011.taste;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.ResponseToHandler;

import android.content.Context;
import android.util.Log;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class Taste {
	
	private String user;
	private Date timeStamp;
	private UserTaste userTaste;
	
	public Taste(String user, Date timeStamp, UserTaste userTaste){
		this.user = user;
		this.timeStamp = timeStamp;
		this.userTaste = userTaste;
	}
	
	public String getUser() { return user; }
	public Date getTimeStamp() { return timeStamp; }	
	public UserTaste getUserTaste() { return userTaste; }
	
	public static ResponseToHandler sendTaste(Context context, String repo, String apkid, String version,String user, String password, UserTaste userTaste) throws IOException, ParserConfigurationException, SAXException{
		
//		http://dev.bazaarandroid.com/webservices/addApkLike/user/<username>/<passhash(sha1)>/<apkid>/<apkversion>/like/<mode>
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException 
		SAXParser sp = spf.newSAXParser();
		String url = String.format(ConfigsAndUtils.TASTE_URL_ADD, user, password, repo, apkid, version, userTaste.toString());
		Log.d("Gen url",url);
		InputStream stream = NetworkApis.getInputStream(context, url);
		BufferedInputStream bstream = new BufferedInputStream(stream);
		
		ResponseToHandler tasteResponseReader = new ResponseToHandler();
		
		sp.parse(new InputSource(bstream), tasteResponseReader);
		
		stream.close();
		bstream.close();
		return tasteResponseReader;  
		
	}
	
}
