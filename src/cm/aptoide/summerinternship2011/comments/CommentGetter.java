/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.EndOfRequestReached;
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
	private ArrayList<Comment> comments;
	private String urlReal;
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 */
	public CommentGetter( String repo, String apkid, String apkversion ) {
		urlReal = String.format(ConfigsAndUtils.COMMENTS_URL_LIST,repo, apkid, apkversion);
	}
	
	public void parse(Context context, int requestSize, BigInteger startFrom, boolean startFromGiven) throws IOException, ParserConfigurationException, SAXException, FactoryConfigurationError {
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		
		InputStream stream = NetworkApis.getInputStream(context, urlReal);
		BufferedInputStream bstream = new BufferedInputStream(stream);
		
		this.status = new StringBuilder("");
    	this.comments = new ArrayList<Comment>();
    	
		sp.parse(new InputSource(bstream), new VersionContentHandler(status, comments, requestSize, startFrom));
		
		stream.close();
		bstream.close();
		
	}
	
	public ArrayList<Comment> getComments() { return comments; }
	
	public Status getStatus() { return Status.valueOfToUpper(status.toString()); }
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class VersionContentHandler extends DefaultHandler{
		
		private CommentElement commentDataIndicator; //null if any element started being read 
		private StringBuilder status;
		private ArrayList<Comment> comments;
		
		private BigInteger id_tmp;
		private String username_tmp;
		private BigInteger answerto_tmp;
		private String subject_tmp;
		private StringBuilder text_tmp;
		private Date timestamp_tmp;
		
		private int requestedSize; // Number of comments requested
		private BigInteger startFrom; // Number of comments requested, starting from comment id 
		private boolean started; // Start reading
		
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, int requestedSize, BigInteger startFrom) {
			
			commentDataIndicator = null;
			this.status = status;
			this.comments = comments;
			
			id_tmp=null; 
			username_tmp=null;
			answerto_tmp=null;
			subject_tmp=null;
			text_tmp = new StringBuilder("");
			timestamp_tmp=null;
			
			this.requestedSize = requestedSize;
			this.startFrom = startFrom;
			started = false;
			
		}
		
		/**
		  * Handle the start of an element.
		  */
		  public void startElement (String uri, String name, String qName, Attributes atts){
			  commentDataIndicator = CommentElement.valueOfToUpper(name); 
		  }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement(String uri, String name, String qName) throws SAXException{
			 	
			 CommentElement elem = CommentElement.valueOfToUpper(name);
			 
			 if(started && elem.equals(CommentElement.ENTRY) ){
				 
				 if(startFrom == null || !id_tmp.equals(startFrom)){
					 if( startFrom == null ) startFrom = id_tmp; 
					 comments.add( new Comment(id_tmp, username_tmp, answerto_tmp, subject_tmp, text_tmp.toString(), timestamp_tmp) );
				 	 if( comments.size() == requestedSize ) 
				 		 throw new EndOfRequestReached();
				 }
				 
				 answerto_tmp = null;
				 subject_tmp = null;
				 text_tmp = new StringBuilder("");
			 }
			 
			 commentDataIndicator = null;
				 
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
					  		if(status.equals(Status.FAIL))
					  			throw new FailedRequestException("Status is failed.");
					  		break;
					 	case ID: 
					 		id_tmp = new BigInteger(read);
					 		if( !started && ( startFrom==null || id_tmp.equals(startFrom)) ){ 
					 			started=true;
					 		}
					 		break;
					  	case USERNAME: 
					  		if(started)
					  			username_tmp = read; 
					  		break;
					  	case ANSWERTO: 
					  		if(started)
					  			answerto_tmp = new BigInteger(read);
					  		break;
					  	case SUBJECT: 
					  		if(started)
					  			subject_tmp = read; 
					  		break;
					  	case TEXT: 
					  		if(started)
					  			text_tmp.append(read);
					  		break;
					  	case TIMESTAMP: 
					  		if(started)
						  		try {
						  			timestamp_tmp = ConfigsAndUtils.TIME_STAMP_FORMAT.parse(read);
						  		} catch (ParseException e) {
						  			throw new FailedRequestException("Date format not valid.");
						  		}
					  		break;
					  	default: break;
					  }
				  
				  }
			  
		  }
		
	}
	
}