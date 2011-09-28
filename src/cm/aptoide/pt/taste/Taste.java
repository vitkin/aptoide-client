package cm.aptoide.pt.taste;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.Configs;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.utils.webservices.ResponseHandler;

import android.content.Context;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public class Taste {
	
	private String user;
	private Date timeStamp;
	private EnumUserTaste userTaste;
	
	/**
	 * 
	 * @param user
	 * @param timeStamp
	 * @param userTaste
	 */
	public Taste(String user, Date timeStamp, EnumUserTaste userTaste){
		this.user = user;
		this.timeStamp = timeStamp;
		this.userTaste = userTaste;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUser() { return user; }
	/**
	 * 
	 * @return
	 */
	public Date getTimeStamp() { return timeStamp; }	
	
	/**
	 * 
	 * @return
	 */
	public EnumUserTaste getUserTaste() { return userTaste; }
	
	/**
	 * 
	 * @param context
	 * @param repo
	 * @param apkid
	 * @param version
	 * @param user
	 * @param password
	 * @param userTaste
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static ResponseHandler sendTaste(Context context, String repo, String apkid, String version, 
											String user, String password, EnumUserTaste userTaste) 
											throws IOException, ParserConfigurationException, SAXException{
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException 
		SAXParser sp = spf.newSAXParser();
		String url = String.format(Configs.WEB_SERVICE_GET_TASTE_ADD, URLEncoder.encode(user), URLEncoder.encode(password), 
										URLEncoder.encode(repo), URLEncoder.encode(apkid), URLEncoder.encode(version), 
										URLEncoder.encode(userTaste.toString()));
		InputStream stream = NetworkApis.getInputStream(context, url);
		BufferedInputStream bstream = new BufferedInputStream(stream);
		
		ResponseHandler tasteResponseReader = new ResponseHandler();
		
		sp.parse(new InputSource(bstream), tasteResponseReader);
		
		stream.close();
		bstream.close();
		return tasteResponseReader;  
		
	}
	
}
