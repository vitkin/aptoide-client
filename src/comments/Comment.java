/**
 * 
 */
package comments;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public final static SimpleDateFormat timeStampFormat = new SimpleDateFormat("y-M-d H:m:s");
	
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
	
}
