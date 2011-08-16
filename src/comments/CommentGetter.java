/**
 * 
 */
package comments;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 * Example of the xml file structure.
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
 */
public class CommentGetter {
	
	//private final static String url = "http://dev.bazaarandroid.com/webservices/listApkComments/%1$s/%2$s/%3$s/xml";
	//http://dev.bazaarandroid.com/webservices/listApkScreens/apps/com.rovio.angrybirds/1.5.1.1
	private final static String url = "http://dev.bazaarandroid.com/webservices/listApkScreens/apps/com.rovio.angrybirds/1.5.1.1";
	
	private StringBuilder status;
	private ArrayList<Comment> versions;
	
	public CommentGetter(Context context, String repo, String apkid, String apkversion) throws MalformedURLException, IOException, ParserConfigurationException, SAXException, FactoryConfigurationError {
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		
		//Careful with UnknownHostException. Throws MalformedURLException, IOException
		HttpURLConnection conn = (HttpURLConnection) new URL(String.format(url,repo, apkid, apkversion)).openConnection();
		
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		
		InputStream stream = conn.getInputStream();
		
		Toast.makeText(context, conn.getContentType()+" "+String.format(url, repo, apkid, apkversion), Toast.LENGTH_LONG).show();
    	
    	this.status= new StringBuilder("");
    	this.versions= new ArrayList<Comment>();
    	
    	sp.parse(new InputSource(new BufferedInputStream(stream)), new VersionContentHandler(status, versions));
        
	}
	
	public ArrayList<Comment> getComments() { return versions; }
	
	public Status getStatus() { return Status.valueOfToUpper(status.toString()); }
	
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 * The default handler for the SAX reader. 
	 * 
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
		
		/**
		 * 
		 * @param pkg
		 */
		public VersionContentHandler(StringBuilder status, ArrayList<Comment> comments) {
			
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
			
		}
		
		/**
		  * Handle the start of an element.
		  */
		  public void startElement (String uri, String name, String qName, Attributes atts){
			  
//			  CommentElement commentElement = CommentElement.valueOfToUpper(name);
//			  
//			  if(commentElement!=null){ commentDataIndicator = commentElement; }
			  
		  }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement (String uri, String name, String qName){
			 
//			 CommentElement elem = CommentElement.valueOfToUpper(name);
//			 
//			 if(elem!=null){  
//				 if(elem.equals(CommentElement.ENTRY))
//					 comments.add(new Comment(id_tmp, username_tmp, answerto_tmp, subject_tmp, text_tmp, timestamp_tmp));
//				 commentDataIndicator = null;
//			 }
			
		  }
		
		  /**
		   * Handle character data.
		   */
		  public void characters (char ch[], int start, int length){
			  
//			  if(commentDataIndicator!=null && commentDataIndicator!=CommentElement.ENTRY){
//				  String read =new String(ch, start, length);
//				  switch(commentDataIndicator){
//				  	case STATUS: status.append(read); break;
//					
//				 	case ID: id_tmp = new BigInteger(read); break;
//				  	case USERNAME: username_tmp = read; break;
//				  	case ANSWERTO: answerto_tmp = new BigInteger(read); break;
//				  	case SUBJECT: subject_tmp = read; break;
//				  	case TEXT: text_tmp = read; break;
//				  	case TIMESTAMP: 
//				  		
//				  		try {
//				  			timestamp_tmp = Comment.timeStampFormat.parse(read);
//				  		} catch (ParseException e) {}
//				  		
//				  		break;
//				  	default: break;
//				  }
//			  
//			  }
			  
		  }
		  
		  public ArrayList<Comment> getComments() { return comments; }
		  
		  public StringBuilder getStatus() { return status; }
		  
		@Override
		public void error(SAXParseException e) throws SAXException {

		}
	
		
		@Override
		public void warning(SAXParseException e) throws SAXException {

		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
		 */
		@Override
		public void endDocument() throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endPrefixMapping(java.lang.String)
		 */
		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
		 */
		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void notationDecl(String name, String publicId, String systemId)
				throws SAXException {
			;
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String, java.lang.String)
		 */
		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
		 */
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			// TODO Auto-generated method stub
			return super.resolveEntity(publicId, systemId);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
		 */
		@Override
		public void setDocumentLocator(Locator locator) {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#skippedEntity(java.lang.String)
		 */
		@Override
		public void skippedEntity(String name) throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
		 */
		@Override
		public void startDocument() throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)
		 */
		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void unparsedEntityDecl(String name, String publicId,
				String systemId, String notationName) throws SAXException {
			
		}
		
		
		
	}
	
}