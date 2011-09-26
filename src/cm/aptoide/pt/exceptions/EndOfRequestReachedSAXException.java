package cm.aptoide.pt.exceptions;

import org.xml.sax.SAXException;

/**
 * @author rafael
 * @since summerinternship2011
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