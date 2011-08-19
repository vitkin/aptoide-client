/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cm.aptoide.summerinternship2011.Configs;

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
	
	@Override
	public String toString() {
		return getSubject()!=null?getSubject():""+Configs.LINE_SEPARATOR+getText()+Configs.LINE_SEPARATOR+getUsername()+" at "+Configs.TIME_STAMP_FORMAT.format(getTimestamp());
	}
	
}
