/**
 * 
 */
package cm.aptoide.summerinternship2011;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class ResponseToHandler extends DefaultHandler{
	
	private Status status;
	private ArrayList<String> errors;
	private StringBuilder read;
	
	private ResponseToElements commentDataIndicator; //null if any element started being read
	
	public ResponseToHandler() {
		errors = new ArrayList<String>();
		status = null;
		read = new StringBuilder("");
	}
	
	/**
	 * Handle the start of an element.
	 */
	 public void startElement (String uri, String name, String qName, Attributes atts){
		 commentDataIndicator = ResponseToElements.valueOfToUpper(name);
	 }
	  
	/**
	 * Handle the end of an element.
	 */
	 public void endElement(String uri, String name, String qName) throws SAXException{
		 
		 if(commentDataIndicator!=null && commentDataIndicator.equals(ResponseToElements.ENTRY)){
			 errors.add( read.toString() );
			 read = new StringBuilder();
			 commentDataIndicator = null;
		 }
	 }
	
	 /**
	  * Handle character data.
	  */
	 public void characters (char ch[], int start, int length) throws SAXException{  
		 if(commentDataIndicator!=null){
			 switch(commentDataIndicator){
				 case STATUS:
					 status = Status.valueOf(new String(ch, start, length));
					 break;
				 case ENTRY:
					 read.append(new String(ch, start, length));
					 break;
				 case ERRORS:
				 case RESPONSE:
				 default: break;
			 }
		 }
	 }
	 
	 public Status getStatus() { return status; }
		
	 public ArrayList<String> getErrors() { return errors; }
	 
}
