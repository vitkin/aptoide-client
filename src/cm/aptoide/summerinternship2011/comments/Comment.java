/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Scanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.Configs;
import cm.aptoide.summerinternship2011.FailedRequestException;
import cm.aptoide.summerinternship2011.Mode;

/**
 * 
 * @author rafael
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
public class Comment implements Comparable<Comment>{
	
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
	 * @param answerto
	 * @param subject
	 * @param text
	 * @param timestamp
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
	
	public int compareTo(Comment otherComment) {
		return this.compareTo(otherComment);
	}
	
	public Bitmap giveQrCode() throws IOException {
		
		StringBuilder strBuilder = new StringBuilder("");
		strBuilder.append(URLEncoder.encode("cht", "UTF-8") + "=" + URLEncoder.encode("qr", "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("chs", "UTF-8") + "=" + URLEncoder.encode("300x300", "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("chl", "UTF-8") + "=" + URLEncoder.encode(this.toString(), "UTF-8"));
		
		
	    URLConnection conn = new URL("https://chart.googleapis.com/chart").openConnection();  
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(strBuilder.toString());
	    wr.flush();
	    
	    conn.connect();  
	    InputStream is = conn.getInputStream();  
	    BufferedInputStream bis = new BufferedInputStream(is);  
	    Bitmap bm = BitmapFactory.decodeStream(bis);  
	    bis.close();  
	    is.close();  
	    return bm;  
	     
	}
	
	
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
	 *	
	 * @param repo
	 * @param apkid
	 * @param version
	 * @return
	 * @throws IOException 
	 * @throws FailedRequestException 
	 */
	public static void sendComment(Context ctx, String repo, String apkid, String version, String text, String user, String pass) throws IOException, FailedRequestException{
		
		HttpURLConnection urlConnection = NetworkApis.send(ctx, Configs.COMMENTS_URL ,repo, apkid, version);
		
		//Variable definition
		StringBuilder strBuilder = new StringBuilder("");
		strBuilder.append(URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("passhash", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("repo", "UTF-8") + "=" + URLEncoder.encode(repo, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("apkid", "UTF-8") + "=" + URLEncoder.encode(apkid, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("apkversion", "UTF-8") + "=" + URLEncoder.encode(version, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8"));
		strBuilder.append("&"+URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(Mode.XML.toString(), "UTF-8"));
		
		//Add variables to request
		urlConnection.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
	    wr.write(strBuilder.toString());
	    wr.flush();
	    
	    //Get response stream
	    urlConnection.connect();  
	    InputStream res = urlConnection.getInputStream();  
	    Scanner sbres = new Scanner(new BufferedInputStream(res));
	    
		if(!sbres.hasNext("<status>OK</status>")){
			throw new FailedRequestException("The server didn't reply as expected.");
		}
	    
	}
	

	@Override
	public String toString() {
		return getSubject()!=null?getSubject():""+Configs.LINE_SEPARATOR+getText()+Configs.LINE_SEPARATOR+getUsername()+" at "+Configs.TIME_STAMP_FORMAT.format(getTimestamp());
	}
	
}
