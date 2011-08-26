package cm.aptoide.summerinternship2011.taste;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.ProtocolException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.FailedRequestException;
import cm.aptoide.summerinternship2011.Status;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 * Example of the xml file structure. When success.
 * 
 * <response>
 * 	<status>OK</status>
 * 	<likes>
 * 		<entry>
 * 			<username>fredde165487</username>
 * 			<timestamp>2011-08-01 13:28:06.727462</timestamp>
 * 		</entry>
 * 	</likes>
 * 	<dislikes>
 * 		<entry>
 * 			<username>Alexandre Fonseca</username>
 * 			<timestamp>2011-08-10 10:06:23.961504</timestamp>
 * 		</entry>
 * 	</dislikes>
 * </response>
 * 
 * Example of the XML file structure. When insuccess.
 * 
 * <response>
 * 	<status>FAIL</status>
 * 	<errors>
 * 		<entry>No apk was found with the given apkid and apkversion.</entry>
 * 	</errors>
 * </response>
 */
public class TasteGetter {
	
	private String urlReal;
	
	private Status status;
	private BigInteger likes;
	private BigInteger dislikes;
	private UserTaste userTaste;
	
	private final static BigInteger UNIT = new BigInteger("1");
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 */
	public TasteGetter( String repo, String apkid, String apkversion) {
		urlReal = String.format(ConfigsAndUtils.TASTE_URL_LIST,repo, apkid, apkversion);
	}
	
	/**
	 * 
	 * @param context
	 * @param username
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FactoryConfigurationError
	 * @throws ProtocolException
	 */
	public void parse(Context context, String username) throws MalformedURLException, IOException, ParserConfigurationException, SAXException, FactoryConfigurationError, ProtocolException {
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		InputStream stream = NetworkApis.getInputStream(context, urlReal);
		VersionContentHandler versionContentHandler = new VersionContentHandler(username);
    	sp.parse(new InputSource(new BufferedInputStream(stream)),versionContentHandler);
    	
    	likes = versionContentHandler.getLikes();
    	dislikes = versionContentHandler.getDislikes();
    	status = versionContentHandler.getStatus();
    	userTaste = versionContentHandler.getUserTaste();
    	
	}
	
	public BigInteger getLikes() { return likes; }
	public BigInteger getDislikes() { return dislikes; }
	public Status getStatus() { return status; }
	public UserTaste getUserTaste() { return userTaste; }
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class VersionContentHandler extends DefaultHandler{
		
		private TasteElement tasteDataIndicator; //null if any element started being read. 
		private TasteElement tasteTypeIndicator; //null if any element started being read. Three possible values null, TasteElement.LIKE, TasteElement.DISLIKE.
		
		private String username;
		
		private Status status;
		private BigInteger likes;
		private BigInteger dislikes;
		private UserTaste userTaste;
		
		/**
		 * 
		 * @param username
		 */
		public VersionContentHandler(String username) {
			
			this.tasteDataIndicator = null;
			this.username = username;
			
			this.status = null;
			this.userTaste = UserTaste.NOTEVALUATED;
			likes = new BigInteger("0");
			dislikes = new BigInteger("0");	
			
		}
		
		public Status getStatus() { return status; }
		public BigInteger getLikes() { return likes; }
		public BigInteger getDislikes() { return dislikes; }
		public UserTaste getUserTaste() { return userTaste; }
		
		/**
		 * Handle the start of an element.
		 */
		 public void startElement (String uri, String name, String qName, Attributes atts){
			 TasteElement tasteElement = TasteElement.valueOfToUpper(name);
			 if(tasteElement.equals(TasteElement.LIKES)||tasteElement.equals(TasteElement.DISLIKES)){
				 tasteTypeIndicator = tasteElement;
			 } else {
				 tasteDataIndicator = tasteElement;
			 }
		 }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement (String uri, String name, String qName) throws SAXException{
			 
			 TasteElement elem = TasteElement.valueOfToUpper(name);
			 if(elem.equals(TasteElement.LIKES)||elem.equals(TasteElement.DISLIKES)){
				 tasteTypeIndicator = null;
			 } else {
				 
				 if(elem.equals(TasteElement.ENTRY)){
					 if(tasteTypeIndicator.equals(TasteElement.LIKES)){
						 likes = likes.add(UNIT);
					 }else if(tasteTypeIndicator.equals(TasteElement.DISLIKES)){
						 dislikes = dislikes.add(UNIT);
					 }
				 }
				 
				 tasteDataIndicator = null;
			 }
				 
		 }
		
		/**
		 * Handle character data.
		 */
		 public void characters (char ch[], int start, int length) throws SAXException{
			 
			 if(tasteDataIndicator!=null){
				 String read = new String(ch, start, length);
				 switch(tasteDataIndicator){
					 case STATUS: 
						 status = Status.valueOfToUpper(read); 
					  	 if(status==null || status.equals(Status.FAIL))
					  		throw new FailedRequestException("The retrived information about the taste was not as expected.");
						 break;
					 case USERNAME: 
						 if(username!= null && read.equals(username)){
							 if(tasteTypeIndicator.equals(TasteElement.LIKES)){
								 userTaste = UserTaste.LIKE;
							 }else if(tasteTypeIndicator.equals(TasteElement.DISLIKES)){
								 userTaste = UserTaste.DONTLIKE;
							 }
						 }
					 case TIMESTAMP:
					 case ENTRY:
					 case RESPONSE:
					 default: break;
				 }
				 
			 }
			 
		 }
		 
		 @Override
		public void endDocument() throws SAXException {
			if(userTaste.equals(UserTaste.NOTEVALUATED))
				userTaste = UserTaste.TASTELESS;
		}
		  
	}
	
	
}
