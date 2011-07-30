package cm.aptoide.summerinternship2011.multiversion.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
/**
 * 
 * @author rafael
 * @since summerinternship2011
 */
public class Utils {
	/**
	 * 
	 * @param uri
	 * @return
	 * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
	 * @throws ProtocolException Thrown to indicate that there is an error in the underlying protocol, such as a TCP error
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 */
	public static InputStream getXMLInputStream(String uri) throws MalformedURLException, ProtocolException, IOException{
		
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		return connection.getInputStream();
	
	}
}
