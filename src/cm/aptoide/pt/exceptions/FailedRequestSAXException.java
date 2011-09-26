/**
 * 
 */
package cm.aptoide.pt.exceptions;

import org.xml.sax.SAXException;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Thrown when an unexpected error in the SAX parser occurs.  
 */
@SuppressWarnings("serial")
public class FailedRequestSAXException extends SAXException{
	
	/**
	 * 
	 * @param msg
	 */
	public FailedRequestSAXException(String msg) { 
		super(msg);
	}
	
}