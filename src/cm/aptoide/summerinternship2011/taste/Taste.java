/**
 * 
 */
package cm.aptoide.summerinternship2011.taste;

import java.util.Date;

/**
 * @author rafael
 *
 */
public class Taste {
	
	private String user;
	private Date timeStamp;
	
	public Taste(String user, Date timeStamp){
		this.user = user;
		this.timeStamp = timeStamp;
	}
	
	public String getUser() { return user; }
	
	public Date getTimeStamp() { return timeStamp; }	
	
}
