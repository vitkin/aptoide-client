/**
 * 
 */
package cm.aptoide.summerinternship2011;

import org.xml.sax.SAXException;

/**
 * 
 * @author rafael
 *
 */
@SuppressWarnings("serial")
public class FailedRequestException extends SAXException{
	
	public FailedRequestException(String msg) {
		super(msg);
	}
	
}