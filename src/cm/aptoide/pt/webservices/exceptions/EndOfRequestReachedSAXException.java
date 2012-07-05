package cm.aptoide.pt.webservices.exceptions;

import org.xml.sax.SAXException;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Thrown if a request to a parser as reach its stop condition.
 */
@SuppressWarnings("serial")
public class EndOfRequestReachedSAXException extends SAXException{
	
	/**
	 * 
	 */
	public EndOfRequestReachedSAXException() {
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public EndOfRequestReachedSAXException(String msg) {
		super(msg);
	}
	
}