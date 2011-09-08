/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.ResponseToHandler;
import cm.aptoide.summerinternship2011.exceptions.FailedRequestException;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 * Example of the xml file structure.
 * 
 * <response>
 * 	<status>OK</status>
 * 	<listing><entry>
 * 		<id>34</id>
 * 		<username>fredde165487</username>
 * 		<answerto/>
 * 		<subject/>
 * 		<text>This app isnt the real market!</text>
 * 		<timestamp>2011-06-05 22:03:08.196793</timestamp>
 * 		</entry>
 * 	</listing>
 * </response>
 * 
 */
public class Comment{
	
	private BigInteger id; 
	private String username;
	private BigInteger answerto;
	private String subject;
	private String text;
	private Date timeStamp;
	
	/**
	 * 
	 * @param id
	 * @param username
	 * @param answerto can be null
	 * @param subject 
	 * @param text 
	 * @param timestamp can not be null
	 */
	public Comment(BigInteger id, String username, BigInteger answerto, String subject, String text, Date timestamp) {
		
		if(id==null || username==null|| text==null || timestamp==null)
			throw new IllegalArgumentException("The arguments id, username, text and timestamp can't be null. Only answerto and subject can be null.");
		
		this.id = id;
		this.username = username;
		this.answerto = answerto;
		this.subject = subject;
		this.text = text;
		this.timeStamp = timestamp; 
		
	}
	
	public Comment(BigInteger id, String username, String text, Date timestamp) {
		this(id, username, null, null, text, timestamp);
	}
	
	/**
	 * @return the id
	 */
	public BigInteger getId() { return id; }
	/**
	 * @return the username
	 */
	public String getUsername() { return username; }
	/**
	 * @return the answerto, null if there is no subject
	 */
	public BigInteger getAnswerto() { return answerto; }
	/**
	 * @return the subject, null if there is no subject
	 */
	public String getSubject() { return subject; }
	/**
	 * @return the text 
	 */
	public String getText() { return text; }
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() { return timeStamp; }
	
	
	/**
	 * 	user - Username
	 *	passhash - SHA1 hash of the user password
	 *	-----
	 *	repo - Repository name
	 *	apkid - Application package ID (example: com.mystuff.android.myapp)
	 *	apkversion - Application version (example: 1.4.2)
	 *	text - Comment text
	 *	mode - Return mode/format ('xml' or 'json')
	 *	
	 * @param ctx
	 * @param repo
	 * @param apkid
	 * @param version
	 * @param subject
	 * @param text
	 * @param user
	 * @param pass
	 * @param reply
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FailedRequestException 
	 */
	public static ResponseToHandler sendComment(
			Context ctx, 
			String repo,
			String apkid, 
			String version,
			String subject,
			String text, 
			String user, 
			String pass,
			BigInteger reply) throws IOException, ParserConfigurationException, SAXException{
		
		HttpURLConnection urlConnection = NetworkApis.send(ctx, ConfigsAndUtils.WEB_SERVICE_POST_COMMENT_ADD,repo, apkid, version);
		
		//Variable definition
		StringBuilder strBuilder = new StringBuilder("");
		strBuilder.append(URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("passhash", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("repo", "UTF-8") + "=" + URLEncoder.encode(repo, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("apkid", "UTF-8") + "=" + URLEncoder.encode(apkid, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("apkversion", "UTF-8") + "=" + URLEncoder.encode(version, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8"));
		if(reply!=null)
			strBuilder.append("&"+URLEncoder.encode("answerto", "UTF-8") + "=" + URLEncoder.encode(reply.toString(), "UTF-8"));
		if(subject!=null && subject.length()!=0)
			strBuilder.append("&"+URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8"));
	    
		//
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		
		//
	    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
	    wr.write(strBuilder.toString());
	    wr.flush();
	    
	    // Get the response
	    SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException 
		SAXParser sp = spf.newSAXParser();
	    ResponseToHandler commentsResponseReader = new ResponseToHandler();
		sp.parse(new InputSource(new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))), commentsResponseReader);
		
		return commentsResponseReader;
		
	}
	

	@Override
	public String toString() {
		String ret = getUsername()+" at "
		+ConfigsAndUtils.TIME_STAMP_FORMAT.format(getTimestamp())
		+ConfigsAndUtils.LINE_SEPARATOR
		+(getSubject()!=null?(getSubject()+ConfigsAndUtils.LINE_SEPARATOR):"")
		+getText();
		return ret;
	}
	
}
