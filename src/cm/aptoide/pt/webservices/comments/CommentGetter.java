package cm.aptoide.pt.webservices.comments;

import java.io.BufferedInputStream;
import java.io.IOException;
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

import cm.aptoide.pt.Configs;
import cm.aptoide.pt.DBHandler;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.webservices.EnumResponseStatus;
import cm.aptoide.pt.webservices.exceptions.CancelRequestSAXException;
import cm.aptoide.pt.webservices.exceptions.EndOfRequestReachedSAXException;
import cm.aptoide.pt.webservices.exceptions.FailedRequestSAXException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Example of the xml file structure. When success.
 * 
 * <response>
 * 	<status>OK</status>
 * 	<listing>
 * 		<entry>
 * 		<id>34</id>
 * 		<username>fredde165487</username>
 * 		<answerto/>
 * 		<subject/>
 * 		<text>This app isnt the real market!</text>
 * 		<timestamp>2011-06-05 22:03:08.196793</timestamp>
 * 		<lang/>
 * 		<useridhash>bdf3b8d237da80e0a51cbdbb44ffcc7dbb143cd8</useridhash>
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
	private ArrayList<String> errors;
	private String urlReal;
	private SAXParser sp;
	private String webservice_path;
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public CommentGetter( String repo, String apkid, String apkversion, long repo_id, Context context) throws ParserConfigurationException, SAXException {
		DBHandler db = new DBHandler(context);
		webservice_path=db.getWebservicespath(repo_id);
		reset(repo, apkid, apkversion);
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		sp = spf.newSAXParser();
		
	}
	
	public void reset(String repo, String apkid, String apkversion){
		if(webservice_path==null){
			urlReal = String.format(Configs.WEB_SERVICE_COMMENTS_LIST,repo, apkid, apkversion);
		}else{
			urlReal = webservice_path+"/"+repo+"/"+apkid+"/"+apkversion;
		}
		
		Log.d("",urlReal);
	}
	
	/**
	 * Request a set of comments starting in startFrom, with comment out selection of a single user.
	 * 
	 * @param context
	 * @param requestSize
	 * @param startFrom
	 * @param startFromGiven
	 * 
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void parse(Context context, int requestSize, BigInteger startFrom, boolean startFromGiven, AsyncTask<?, ?, ?> callTask) throws IOException, SAXException {
		
		BufferedInputStream bstream = buildBasicStructure(context);
		sp.parse(new InputSource(bstream), new VersionContentHandler(status, comments, errors, requestSize, startFrom, callTask));
		bstream.close();
		
	}
	
	/**
	 * Request comments until a certain comment is found excluding this one, given by the variable until, with comment out selection of a single user.
	 * 
	 * @param context
	 * @param until Get until this comment
	 * 
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void parse(Context context, BigInteger until) throws IOException, SAXException {
		
		BufferedInputStream bstream = buildBasicStructure(context);
		sp.parse(bstream, new VersionContentHandler(status, comments, errors, until));
		bstream.close();
		
	}
	
	/**
	 * Request comments until a certain comment is found excluding this one, given by the variable until, with comment selection of a single user.
	 * 
	 * @param context
	 * @param until Get until this comment
	 * @param fromUserIdHash User id hash whose comments are being selected. Null to select all comments
	 * 
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void parse(Context context, BigInteger until, String fromUserIdHash) throws IOException, SAXException {
		
		BufferedInputStream bstream = buildBasicStructure(context);
		sp.parse(bstream, new VersionContentHandler(status, comments, errors, until,fromUserIdHash));
		bstream.close();
		
	}
	
	/**
	 * Request a set of comments starting in startFrom, with comment selection of a single user.
	 * 
	 * @param context
	 * @param requestSize
	 * @param startFrom
	 * @param startFromGiven
	 * @param fromUserIdHash User id hash whose comments are being selected. Null to select all comments
	 * 
	 * @throws IOException
	 * @throws SAXException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 */
	public void parse(Context context, int requestSize, BigInteger startFrom, boolean startFromGiven, String fromUserIdHash) throws IOException, SAXException {
		
		BufferedInputStream bstream = buildBasicStructure(context);
		sp.parse(new InputSource(bstream), new VersionContentHandler(status, comments, errors, requestSize, startFrom, fromUserIdHash, null));
		bstream.close();
		
	}
	
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private BufferedInputStream buildBasicStructure(Context context) throws IOException{
		this.status = new StringBuilder("");
    	this.comments = new ArrayList<Comment>();
    	this.errors = new ArrayList<String>();
    	return new BufferedInputStream(NetworkApis.getInputStream(context, urlReal));
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Comment> getComments() { return comments; }
	
	/**
	 * 
	 * @return
	 */
	public EnumResponseStatus getStatus() { return EnumResponseStatus.valueOfToUpper(status.toString()); }
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getErrors() { return errors; }
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public static class VersionContentHandler extends DefaultHandler{
		
		private EnumResponseCommentElement 	commentDataIndicator; 	//Null if any element started being read 
		private StringBuilder 				status;
		private ArrayList<Comment> 			comments;
		private ArrayList<String> 			errors;
		
		private BigInteger 					id_tmp;
		private String 						username_tmp;
		private BigInteger 					answerto_tmp;
		private String 						subject_tmp;
		private StringBuilder 				text_tmp;
		private Date 						timestamp_tmp;
		private String 						userIdHash_tmp;
		
		private int 						requestedSize; 			// Number of comments requested
		private BigInteger 					startFrom; 				// Number of comments requested, starting from comment id 
		private boolean 					started; 				// Start reading?
		private BigInteger 					until;
		private String 						fromUserIdHash; 		// If set only get comments from this user id. Note: That the user ID is the sha-1 hash of the users email
		
		private AsyncTask<?, ?, ?> 			callTask;
		
		
		/**
		 * Request a set of comments starting in startFrom.
		 * 
		 * @param status If the connection was performed successfully
		 * @param comments The Collection to put the comments
		 * @param errors
		 * @param requestedSize The number of comments to get
		 * @param startFrom If start from is null it will start gathering comments from the beginning of the xml file
		 * @param fromUserIdHash User id hash whose comments are being selected. Null to select all comments
		 */
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, ArrayList<String> errors, int requestedSize, BigInteger startFrom, String fromUserIdHash, AsyncTask<?, ?, ?> callTask) {
			
			commentDataIndicator = null;
			this.status = status;
			this.comments = comments;
			this.errors = errors;
			
			id_tmp=null; 
			username_tmp=null;
			answerto_tmp=null;
			subject_tmp=null;
			text_tmp = new StringBuilder("");
			timestamp_tmp=null;
			userIdHash_tmp = null;
			
			this.requestedSize = requestedSize;
			this.startFrom = startFrom;
			started = false;
			
			//Not used in this operation mode
			until = null;
			
			this.fromUserIdHash = null;
			
			this.callTask = callTask;
			
		}
		
		/**
		 * Request comments until a certain comment is found excluding this one given by the variable until. 
		 * 
		 * @param status If the connection was performed successfully
		 * @param comments The Collection to put the comments
		 * @param errors
		 * @param until Get the comments until this comment id can not be null
		 * @param fromUserIdHash User id hash whose comments are being selected. Null to select all comments
		 */
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, ArrayList<String> errors, 
										BigInteger until, String fromUserIdHash) {
			this(status, comments, errors, 0, null, fromUserIdHash, null);
			if(until==null)
				throw new IllegalArgumentException("The until parameter can not be null");
			this.until = until;
			//Not used in this operation mode
			//this.requestedSize = 0;
			//this.startFrom = null;
		}
		
		/**
		 * Request comments until a certain comment is found excluding this one given by the variable until, with out comment selection of a single user.
		 * 
		 * @param status If the connection was performed successfully
		 * @param comments The Collection to put the comments
		 * @param errors
		 * @param until Get the comments until this comment id can not be null
		 */
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, ArrayList<String> errors, BigInteger until) {
			this(status, comments, errors, 0, null, null, null);
		}
		
		/**
		 * Request a set of comments starting in startFrom, with out comment selection of a single user.
		 * 
		 * @param status If the connection was performed successfully
		 * @param comments The Collection to put the comments
		 * @param errors
		 * @param requestedSize The number of comments to get
		 * @param startFrom If start from is null it will start gathering comments from the beginning of the xml file
		 */
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments, ArrayList<String> errors, 
											int requestedSize, BigInteger startFrom, AsyncTask<?, ?, ?> callTask) {
			
			this(status, comments, errors, requestedSize, startFrom, null, callTask);
			
		}
		
		/**
		  * Handle the start of an element.
		  */
		  public void startElement (String uri, String name, String qName, Attributes atts){
			  commentDataIndicator = EnumResponseCommentElement.valueOfToUpper(name); 
		  }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement(String uri, String name, String qName) throws SAXException{
			
			 if(callTask!=null && callTask.isCancelled())
				 throw new CancelRequestSAXException();
			 
			 EnumResponseCommentElement elem = EnumResponseCommentElement.valueOfToUpper(name);
			 
			 if(started && elem.equals(EnumResponseCommentElement.ENTRY) ){
				 
				 if(until!=null){
					 if(!id_tmp.equals(until)){
						 if(fromUserIdHash==null||fromUserIdHash.equals(userIdHash_tmp)){
							 // If the commentaries selection of a single user is turned on
							 comments.add( new Comment(id_tmp, username_tmp, answerto_tmp, subject_tmp, text_tmp.toString(), timestamp_tmp) );
						 
						 }
					 }else{ 
						 throw new EndOfRequestReachedSAXException();
					 }
				 }else if(startFrom == null || !id_tmp.equals(startFrom)){
					 if( startFrom == null ) startFrom = id_tmp; 
					 comments.add( new Comment(id_tmp, username_tmp, answerto_tmp, subject_tmp, text_tmp.toString(), timestamp_tmp) );
				 	 if( comments.size() == requestedSize ) 
				 		 throw new EndOfRequestReachedSAXException();
				 }
				 
				 answerto_tmp = null;
				 subject_tmp = null;
				 text_tmp = new StringBuilder("");
				 
			 } else if(status.equals(EnumResponseStatus.FAIL) && elem.equals(EnumResponseCommentElement.ENTRY)){
				 errors.add(text_tmp.toString());
				 text_tmp = new StringBuilder("");
			 }
			 
			 commentDataIndicator = null;
				 
		  }
		
		  /**
		   * Handle character data.
		   */
		  public void characters (char ch[], int start, int length) throws SAXException{
			  
				  if(commentDataIndicator!=null && commentDataIndicator!=EnumResponseCommentElement.ENTRY){
					  String read =new String(ch, start, length);
					  switch(commentDataIndicator){
					  	case STATUS: 
					  		status.append(read);
					  		break;
					 	case ID: 
					 		id_tmp = new BigInteger(read);
					 		if(!started && ( until!=null  || ( startFrom==null || id_tmp.equals(startFrom) ) ) ){ 
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
						  			timestamp_tmp = Configs.TIME_STAMP_FORMAT.parse(read);
						  		} catch (ParseException e) {
						  			throw new FailedRequestSAXException("Date format not valid.");
						  		}
					  		break;
					  	case ENTRY:
					  		if( status.equals(EnumResponseStatus.FAIL) )
					  			text_tmp.append(read);
					  		break;
					  	case USERIDHASH:
					  		if(fromUserIdHash!=null)
					  			userIdHash_tmp = read;
					  		break;
					  	default: break;
					  }
				  
				  }
			  
		  } // End characters
		
	} // End VersionContentHandler
	
}