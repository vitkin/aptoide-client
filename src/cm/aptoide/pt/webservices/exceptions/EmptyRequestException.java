package cm.aptoide.pt.webservices.exceptions;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Used in for example when a fetch form a xml file results in 0 elements.
 */
@SuppressWarnings("serial")
public class EmptyRequestException extends Exception{
	
	/**
	 * 
	 */
	public EmptyRequestException() {
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public EmptyRequestException(String msg) {
		super(msg);
	}
	
}
