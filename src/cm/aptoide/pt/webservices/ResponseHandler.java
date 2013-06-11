/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.webservices;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;


/**
 * @author rafael
 * @since 2.5.3
 *
 * The handler that provides information about server response, for:
 * 	+	Comment Post;
 * 	+	Rating;
 * 	+	Login. In login the token element is ignored;
 */
public class ResponseHandler extends DefaultHandler{

	private EnumResponseStatus status;
	private ArrayList<String> errors;
	private StringBuilder read;

	private EnumSimpleResponse commentDataIndicator; //null if any element started being read

	/**
	 *
	 */
	public ResponseHandler() {
		errors = new ArrayList<String>();
		status = null;
		read = new StringBuilder("");
	}

	/**
	 * Handle the start of an element.
	 */
	 public void startElement (String uri, String name, String qName, Attributes atts){
		 commentDataIndicator = EnumSimpleResponse.valueOfToUpper(name);
	 }

	/**
	 * Handle the end of an element.
	 */
	 public void endElement(String uri, String name, String qName) throws SAXException{

		 if(commentDataIndicator!=null && commentDataIndicator.equals(EnumSimpleResponse.ENTRY)){
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
					 status = EnumResponseStatus.valueOf(new String(ch, start, length));
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

	 public EnumResponseStatus getStatus() { return status; }

	 public ArrayList<String> getErrors() { return errors; }

}
