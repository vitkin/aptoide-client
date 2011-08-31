/**
 * 
 */
package cm.aptoide.summerinternship2011.exceptions;

/**
 * @author rafael
 * @since summerinternship2011
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
