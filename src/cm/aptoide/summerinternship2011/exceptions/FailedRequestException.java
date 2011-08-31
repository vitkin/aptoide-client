/**
 * 
 */
package cm.aptoide.summerinternship2011.exceptions;

import org.xml.sax.SAXException;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Thrown when an unexpected error in the SAX parser occurs.  
 */
@SuppressWarnings("serial")
public class FailedRequestException extends SAXException{
	
	/**
	 * 
	 * @param msg
	 */
	public FailedRequestException(String msg) { 
		super(msg);
	}
	
}