/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.ProtocolException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.Configs;
import cm.aptoide.summerinternship2011.FailedRequestException;
import cm.aptoide.summerinternship2011.Status;

import android.content.Context;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 * Example of the xml file structure. When success.
 * 
 * <response>
 * 	<status>OK</status>
 * 	<listing><entry>
 * 		<id>34</id>
 * 		<username>fredde165487</username>
 * 		<answerto/>
 * 		<subject/>
 * 		<text>This app isnt the real market!</text>
 * 		<timestamp>2011-06-05 22:03:08.196793</timestamp>
 * 		</entry>
 * 	</listing>
 * </response>
 * 
 * Example of the xml file structure. When insuccess.
 * 
 * <response>
 * 	<status>FAIL</status>
 * 	<errors>
 * 		<entry>No apk was found with the given apkid and apkversion.</entry>
 * 	</errors>
 * </response>
 * 
 */
public class CommentGetter {
	
	private StringBuilder status;
	private ArrayList<Comment> versions;
	private String urlReal;
	
	public CommentGetter( String repo, String apkid, String apkversion) {
		
		urlReal = String.format(Configs.COMMENTS_URL,repo, apkid, apkversion);
    	
	}
	
	public void parse(Context context, int requestSize, BigInteger startFrom) throws MalformedURLException, IOException, ParserConfigurationException, SAXException, FactoryConfigurationError, ProtocolException {
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		InputStream stream = NetworkApis.getInputStream(context, urlReal);
		this.status= new StringBuilder("");
    	this.versions= new ArrayList<Comment>();
		sp.parse(new InputSource(new BufferedInputStream(stream)), new VersionContentHandler(status, versions, requestSize, startFrom));
	}
	
	public ArrayList<Comment> getComments() { return versions; }
	
	public Status getStatus() { return Status.valueOfToUpper(status.toString()); }
	
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 * The default handler for the SAX reader.
	 */
	public class VersionContentHandler extends DefaultHandler{
		
		/*
		 * null if any element started being read 
		 */
		private CommentElement commentDataIndicator;
		private StringBuilder status;
		private ArrayList<Comment> comments;
		
		/*
		 * 
		 */
		private BigInteger id_tmp;
		private String username_tmp;
		private BigInteger answerto_tmp;
		private String subject_tmp;
		private String text_tmp;
		private Date timestamp_tmp;
		
		private int requestedSize;
		private BigInteger startFrom;
		private boolean started;
		
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, int requestedSize, BigInteger startFrom) {
			
			this.status = status;
			this.comments = comments;
			
			commentDataIndicator = null;
			
			id_tmp=null; 
			username_tmp=null;
			answerto_tmp=null;
			subject_tmp=null;
			text_tmp=null;
			timestamp_tmp=null;
			status = null;
			
			this.requestedSize = requestedSize;
			this.startFrom = startFrom;
			started = false;
		}
		
		/**
		  * Handle the start of an element.
		  */
		  public void startElement (String uri, String name, String qName, Attributes atts){
			  
		 	  CommentElement commentElement = CommentElement.valueOfToUpper(name);
			  if(commentElement!=null){ commentDataIndicator = commentElement; }
			  
			  
		  }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement (String uri, String name, String qName) throws SAXException{
			 	
				 CommentElement elem = CommentElement.valueOfToUpper(name);
				 if(elem!=null){  
					 if(elem.equals(CommentElement.ENTRY) && ((started && !id_tmp.equals(startFrom))||startFrom==null) ){
						 comments.add(new Comment(id_tmp, username_tmp, answerto_tmp, subject_tmp, text_tmp, timestamp_tmp));
						 if(startFrom!=null && comments.size()==requestedSize){
							 throw new EndOfRequestReached();
						 }
					 }
					 commentDataIndicator = null;
				 }
				 
		  }
		
		  /**
		   * Handle character data.
		   */
		  public void characters (char ch[], int start, int length) throws SAXException{
			  
				  if(commentDataIndicator!=null && commentDataIndicator!=CommentElement.ENTRY){
					  String read =new String(ch, start, length);
					  switch(commentDataIndicator){
					  	case STATUS: 
					  		status.append(read); 
					  		if(status.equals(Status.FAILED))
					  			throw new FailedRequestException("Status is failed.");
					  		break;
						
					 	case ID: 
					 		id_tmp = new BigInteger(read); 
					 		if(startFrom!=null && id_tmp.equals(startFrom))
					 			started=true;
					 		break;
					  	case USERNAME: username_tmp = read; break;
					  	case ANSWERTO: answerto_tmp = new BigInteger(read); break;
					  	case SUBJECT: subject_tmp = read; break;
					  	case TEXT: text_tmp = read; break;
					  	case TIMESTAMP: 
					  		
					  		try {
					  			timestamp_tmp = Configs.TIME_STAMP_FORMAT.parse(read);
					  		} catch (ParseException e) {
					  			throw new FailedRequestException("Parse exception while parsing date.");
					  		}
					  		
					  		break;
					  	default: break;
					  }
				  
				  }
			  
		  }
		
	}
	
	@SuppressWarnings("serial")
	public static class EndOfRequestReached extends SAXException{}
	
}