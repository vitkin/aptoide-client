package cm.aptoide.pt.taste;

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
import android.os.AsyncTask;
import cm.aptoide.pt.Configs;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.exceptions.CancelRequestSAXException;
import cm.aptoide.pt.utils.webservices.EnumResponseStatus;

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
 * 			<username>Mário Medina Costa</username>
 * 			<timestamp>2011-09-05 15:21:10.60119</timestamp>
 *			<useridhash>3c9fdc93aa8cfeaab7c1b531d958bc25ed009ea2</useridhash>
 * 		</entry>
 * 	</likes>
 * 	<dislikes>
 * 		<entry>
 * 			<username>masoud_tnb5</username>
 * 			<timestamp>2011-09-05 15:21:10.60119</timestamp>
 * 			<useridhash>6587642e853bdab272175a23f139e7eeb6b08546</useridhash>
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
	
	private EnumResponseStatus status;
	private ArrayList<String> errors;
	private BigInteger likes;
	private BigInteger dislikes;
	private EnumUserTaste userTaste;
	private AsyncTask<?,?,?> submitTaste;
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 */
	public TasteGetter( String repo, String apkid, String apkversion) {
		urlReal = String.format(Configs.WEB_SERVICE_TASTE_LIST, URLEncoder.encode(repo), URLEncoder.encode(apkid), URLEncoder.encode(apkversion));
	}
	
	/**
	 * 
	 * @param context
	 * @param useridLogin
	 * @param submitTaste
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException 
	 */
	public void parse(Context context, String useridLogin, AsyncTask<?,?,?> submitTaste) throws ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParser sp = spf.newSAXParser();
		InputStream stream = NetworkApis.getInputStream(context, urlReal);
		VersionContentHandler versionContentHandler = new VersionContentHandler(useridLogin);
		this.submitTaste = submitTaste;
		sp.parse(new InputSource(new BufferedInputStream(stream)), versionContentHandler);
    	
    	likes = versionContentHandler.getLikes();
    	dislikes = versionContentHandler.getDislikes();
    	status = versionContentHandler.getStatus();
    	userTaste = versionContentHandler.getUserTaste();
    	errors = versionContentHandler.getErrors();
    	
	}
	
	public BigInteger getLikes() { return likes; }
	public BigInteger getDislikes() { return dislikes; }
	public EnumResponseStatus getStatus() { return status; }
	public EnumUserTaste getUserTaste() { return userTaste; }
	public ArrayList<String> getErrors() { return errors; }
	
	
	
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class VersionContentHandler extends DefaultHandler{
		
		private EnumResponseTasteElement tasteDataIndicator; //null if any element started being read. 
		private EnumResponseTasteElement tasteTypeIndicator; //null if any element started being read. Three possible values null, TasteElement.LIKE, TasteElement.DISLIKE.
		
		private String useridLogin;
		
		private EnumResponseStatus status;
		private BigInteger likes;
		private BigInteger dislikes;
		private EnumUserTaste userTaste;
		private ArrayList<String> errors;
		private StringBuilder error;
		/**
		 * 
		 * @param useridLogin
		 */
		public VersionContentHandler(String useridLogin) {
			
			this.tasteDataIndicator = null;
			this.useridLogin = useridLogin;
			
			this.status = null;
			this.userTaste = EnumUserTaste.NOTEVALUATED;
			likes = new BigInteger("0");
			dislikes = new BigInteger("0");
			errors = new ArrayList<String>();
			error = new StringBuilder("");
		}
		
		public EnumResponseStatus getStatus() { return status; }
		public BigInteger getLikes() { return likes; }
		public BigInteger getDislikes() { return dislikes; }
		public EnumUserTaste getUserTaste() { return userTaste; }
		public ArrayList<String> getErrors() { return errors; }
		
		/**
		 * Handle the start of an element.
		 */
		 public void startElement (String uri, String name, String qName, Attributes atts){
			 EnumResponseTasteElement tasteElement = EnumResponseTasteElement.valueOfToUpper(name);
			 if(tasteElement.equals(EnumResponseTasteElement.LIKES)||tasteElement.equals(EnumResponseTasteElement.DISLIKES)){
				 tasteTypeIndicator = tasteElement;
			 } else {
				 tasteDataIndicator = tasteElement;
			 }
		 }
		  
		/**
		 * Handle the end of an element.
		 */
		 public void endElement (String uri, String name, String qName) throws SAXException{
			 
			 EnumResponseTasteElement elem = EnumResponseTasteElement.valueOfToUpper(name);
			 if(elem.equals(EnumResponseTasteElement.LIKES) || elem.equals(EnumResponseTasteElement.DISLIKES)){
				 tasteTypeIndicator = null;
			 } else {
				 
				 if(elem.equals(EnumResponseTasteElement.ENTRY)){
					 if(submitTaste.isCancelled()){
						 //Check if request as been canceled
						 throw new CancelRequestSAXException();
					 }
					 if(status.equals(EnumResponseStatus.OK)){
						 
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
						status = EnumResponseStatus.valueOfToUpper(read); 
					  	break;
					 case USERIDHASH:
						// Log.d("REad blá",read+" "+useridLogin);
						 if(useridLogin!= null && read.equals(useridLogin)){
							 
							 switch(tasteTypeIndicator){
							 	case LIKES: userTaste = EnumUserTaste.LIKE; break; 
							 	case DISLIKES: userTaste = EnumUserTaste.DONTLIKE; break;
							 }
						 }break;
					 
					 case ENTRY:
						 if(status.equals(EnumResponseStatus.FAIL)){
							 error.append(read);
						 }break;
					 case TIMESTAMP:
					 case USERNAME:
					 case RESPONSE:
					 default: break;
				 }
			 
		 }
		 
		 @Override
		public void endDocument() throws SAXException {
			if(userTaste.equals(EnumUserTaste.NOTEVALUATED))
				userTaste = EnumUserTaste.TASTELESS;
		}
		  
	}
	
	
	
	
	
}
