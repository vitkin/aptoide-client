package cm.aptoide.summerinternship2011;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Parses the XML file containing the the critical version information.
 * This is a DOM implementation this meaning that the parsed file is dumped into memory. This could be critical if the file
 * is too large. How large? This is another question. As the file grows the time to dump the file should rise as well.
 * If this implementation doesn't meet your application needs you should implement a SAX approach based on streaming.
 */
public class VersionParser {
	
	/**
	 * 
	 * @author rafael
	 * Resource type
	 * 
	 */
	public enum Source{ FILE, WEB }
	/**
	 * Resource path
	 */
	private String sourcePath;
	/**
	 * 
	 */
	private Document dom;
	/**
	 * 
	 */
	private Source source;
	
	/**
	 * 
	 * @param sourcePath
	 * @param source
	 * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
	 * @throws ProtocolException Thrown to indicate that there is an error in the underlying protocol, such as a TCP error
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 * @throws ParserConfigurationException Indicates a serious configuration error
	 * @throws SAXException This class can contain basic error or warning information from either the XML parser or the application: a parser writer or application writer can subclass it to provide additional functionality. SAX handlers may throw this exception or any exception subclassed from it
	 */
	public VersionParser(String	sourcePath, Source source) throws MalformedURLException, ProtocolException, IOException, ParserConfigurationException, SAXException {
		this.sourcePath = sourcePath; 
		this.source = source;
		
		switch(this.source){
		case FILE: 
			dom = parseXmlFile( new File(this.sourcePath) );
			break;
		case WEB: 
			InputStream xmlInputStream = null;
			try{
				xmlInputStream = getXMLInputStream(this.sourcePath); 
				dom =  parseXmlInputStream(xmlInputStream);
			}finally{xmlInputStream.close();}
			break;
		default:  throw new IllegalArgumentException("Choose a valid source for XML parsing");
		}
	}
	
	/**
	 * 
		
	 * @return
	 * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
	 * @throws ProtocolException Thrown to indicate that there is an error in the underlying protocol, such as a TCP error
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 */
	public String getXMLraw() throws MalformedURLException, ProtocolException, IOException{
		Scanner reader = null;
		StringBuilder bufferOutput = new StringBuilder("");
		
		switch(this.source){
			
		case FILE:
			reader = new Scanner(new File(this.sourcePath));
			break;
		case WEB:
			reader = new Scanner(new BufferedInputStream(getXMLInputStream(this.sourcePath)));
			break;
		default: break;
			
		}
		
		while(reader.hasNextLine()){bufferOutput.append(reader.nextLine()+"\r\n");}
		
		return bufferOutput.toString();
	}
	
	/**
	 * 
	 * @return A hashtable with keys String (VAlues.toString()) and with values VersionApk
	 */
	public Hashtable<String, VersionApk> getVersions(){
		Hashtable<String, VersionApk> versions = new Hashtable<String, VersionApk>();
		
		try {
			// Get a NodeList of  elements
			NodeList nl = dom.getElementsByTagName("version");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0; i < nl.getLength();i++) {
					
					String version = getVersionTagText(i, "versionNumber", nl);			
						versions.put(version, new VersionApk(version
								,getVersionTagText(i, "uri", nl)
								,getVersionTagText(i, "md5", nl)));
				}
			}
		} catch (MalformedURLException e) {
			//If the URL was malformed this exception had already been thrown in the constructor.
			//Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
			e.printStackTrace();
		}
		
		return versions;
		
	}
	
	/**
	 * 
	 * @return The app location
	 */
	public String getPkg(){
		return getVersionTagText(0, "pkg", dom.getElementsByTagName("app"));
	}
	
	
	/**
	 * 
	 * @param number
	 * @param tag
	 * @param parentNode
	 * @return The String value for a given field within a NodeList 
	 */
	private String getVersionTagText(int number, String tag, NodeList parentNode){
		return ((Element)parentNode.item(number)).getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws ParserConfigurationException Indicates a serious configuration error. 
	 * @throws SAXException This class can contain basic error or warning information from either the XML parser or the application: a parser writer or application writer can subclass it to provide additional functionality. SAX handlers may throw this exception or any exception subclassed from it
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 */
	private Document parseXmlFile(File file) throws ParserConfigurationException, SAXException, IOException{
		//Get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//Using factory get an instance of document builder
		DocumentBuilder db = dbf.newDocumentBuilder();
		//Parse using builder to get DOM representation of the XML file
		return db.parse(file);
	}
	
	/**
	 * 
	 * @param xmlInputStream
	 * @return
	 * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
	 * @throws ProtocolException Thrown to indicate that there is an error in the underlying protocol, such as a TCP error
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 * @throws ParserConfigurationException Indicates a serious configuration error. 
	 * @throws SAXException This class can contain basic error or warning information from either the XML parser or the application: a parser writer or application writer can subclass it to provide additional functionality. SAX handlers may throw this exception or any exception subclassed from it
	 */
	private Document parseXmlInputStream(InputStream xmlInputStream) throws MalformedURLException, ProtocolException, IOException, ParserConfigurationException, SAXException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xmlInputStream);
		return doc;
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.Either no legal protocol could be found in a specification string or the string could not be parsed
	 * @throws ProtocolException Thrown to indicate that there is an error in the underlying protocol, such as a TCP error
	 * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations
	 */
	private InputStream getXMLInputStream(String uri) throws MalformedURLException, ProtocolException, IOException{
		
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		return connection.getInputStream();
	
	}
	
}
