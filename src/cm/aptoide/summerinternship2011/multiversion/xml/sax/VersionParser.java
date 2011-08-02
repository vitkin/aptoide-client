/**
 * 
 */
package cm.aptoide.summerinternship2011.multiversion.xml.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import android.content.Context;

import cm.aptoide.summerinternship2011.multiversion.VersionApk;
/**
 * 
 * @author rafael
 *
 */
public class VersionParser {
	
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
	
	private String url;
	
	private StringBuilder pkg;
	private ArrayList<VersionApk> versions;
	
	public VersionParser(String url, InputStream schemaSource, Context context) throws MalformedURLException, IOException, ParserConfigurationException, SAXException, FactoryConfigurationError {
		
		//Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError 
		SAXParserFactory spf = SAXParserFactory.newInstance();
//		spf.setNamespaceAware(true);
//		spf.setValidating(true);
//		spf.setFeature("validation", true);
//		spf.setFeature("namespaces", true);
		
		SAXParser sp = spf.newSAXParser();
		
		try {
			sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);//Thrown hear
			sp.setProperty(JAXP_SCHEMA_SOURCE, schemaSource);
		} catch (IllegalArgumentException e) {
		  // Happens if the parser does not support JAXP 1.2
		} catch(SAXNotRecognizedException e){
	       //Your SAX parser is not JAXP 1.2 compliant.
		}
		
		pkg = new StringBuilder();
        versions = new ArrayList<VersionApk>();
        
        InputStream stream = null;
        
//        switch(resourceSource){
//	        case FILE: stream = new FileInputStream(path); break;
//	        case WEB: default: new IllegalArgumentException("Source "+resourceSource.toString()+" not recognizable.");
//        }
        
        //Careful with UnknownHostException
		//Throws MalformedURLException, IOException
    	stream = new URL(url).openStream();
    	
        sp.parse(new InputSource(stream), new VersionContentHandler(pkg, versions));
        
        this.url = url;
        
	}
	
	public String getPkg() {
		return pkg.toString();
	}
	
	public ArrayList<VersionApk> getVersions() {
		return versions;
	}
	
	public String getUrl() {
		return url;
	}
	
}
