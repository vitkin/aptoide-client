package cm.aptoide.summerinternship2011.taste;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.Status;

/**
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
 * 
 */
public class TasteGetter {
	
	private String urlReal;
	
	private Status status;
	private ArrayList<String> errors;
	private BigInteger likes;
	private BigInteger dislikes;
	private UserTaste userTaste;
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 */
	public TasteGetter( String repo, String apkid, String apkversion) {
		urlReal = String.format(ConfigsAndUtils.TASTE_URL_LIST, URLEncoder.encode(repo), URLEncoder.encode(apkid), URLEncoder.encode(apkversion));
		
	}
	
	/**
	 * 
	 * @param context
	 * @param username
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException 
	 */
	public void parse(Context context, String username) throws ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		InputStream stream = NetworkApis.getInputStream(context, urlReal);
		VersionContentHandler versionContentHandler = new VersionContentHandler(username);
    	sp.parse(new InputSource(new BufferedInputStream(stream)), versionContentHandler);
    	
    	likes = versionContentHandler.getLikes();
    	dislikes = versionContentHandler.getDislikes();
    	status = versionContentHandler.getStatus();
    	userTaste = versionContentHandler.getUserTaste();
    	errors = versionContentHandler.getErrors();
	}
	
	public BigInteger getLikes() { return likes; }
	public BigInteger getDislikes() { return dislikes; }
	public Status getStatus() { return status; }
	public UserTaste getUserTaste() { return userTaste; }
	public ArrayList<String> getErrors() { return errors; }
	
	
	
	
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
		private ArrayList<String> errors;
		private StringBuilder error;
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
			errors = new ArrayList<String>();
			error = new StringBuilder("");
		}
		
		public Status getStatus() { return status; }
		public BigInteger getLikes() { return likes; }
		public BigInteger getDislikes() { return dislikes; }
		public UserTaste getUserTaste() { return userTaste; }
		public ArrayList<String> getErrors() { return errors; }
		
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
					 if(status.equals(Status.OK)){
						 
						 switch(tasteTypeIndicator){
							 case LIKES: 
								 likes = likes.add(BigInteger.ONE);
								 break;
							 case DISLIKES: 
								 dislikes = dislikes.add(BigInteger.ONE);
								 break;
							 default: break;
						 }
						 
					 }else{
						 //Error...
						 errors.add(error.toString());
						 error = new StringBuilder("");
					 }
					 
				 }
				 tasteDataIndicator = null;
			 }
				 
		 }
		
		/**
		 * Handle character data.
		 */
		 public void characters (char ch[], int start, int length) throws SAXException{
			 
				 String read = new String(ch, start, length);
				 switch(tasteDataIndicator){
					 case STATUS: 
						status = Status.valueOfToUpper(read); 
					  	break;
					 case USERNAME: 
						 if(username!= null && read.equals(username)){
							 switch(tasteTypeIndicator){
							 	case LIKES: userTaste = UserTaste.LIKE; break; 
							 	case DISLIKES: userTaste = UserTaste.DONTLIKE;
							 }
						 }
					 case TIMESTAMP:
					 case ENTRY:
						 if(status.equals(Status.FAIL)){
							 error.append(read);
						 }
					 case RESPONSE:
					 default: break;
				 }
			 
		 }
		 
		 @Override
		public void endDocument() throws SAXException {
			if(userTaste.equals(UserTaste.NOTEVALUATED))
				userTaste = UserTaste.TASTELESS;
		}
		  
	}
	
	
	
	
	
}
