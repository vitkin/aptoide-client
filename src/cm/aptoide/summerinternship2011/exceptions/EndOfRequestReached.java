package cm.aptoide.summerinternship2011.exceptions;

import org.xml.sax.SAXException;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Thrown if a request to a parser as reach its stop condition.
 */
@SuppressWarnings("serial")
public class EndOfRequestReached extends SAXException{
	
	/**
	 * 
	 */
	public EndOfRequestReached() {
		super();
	}
	
	/**
	 * 
	 * @param msg
	 */
	public EndOfRequestReached(String msg) {
		super(msg);
	}
	
}