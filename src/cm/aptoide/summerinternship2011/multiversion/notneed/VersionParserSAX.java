///**
// * 
// */
//package cm.aptoide.summerinternship2011.multiversion.notneed;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.XMLReaderFactory;
//
//import cm.aptoide.summerinternship2011.ResourceSource;
//import cm.aptoide.summerinternship2011.multiversion.VersionApk;
//import cm.aptoide.summerinternship2011.multiversion.xml.sax.VersionContentHandler;
//
///**
// * @author rafael
// * @since summerinternship2011
// * 
// * Creates a xml version data parser reader more efficient than the VersionParserDOM.java even thought less features are supported.
// * 
// */
//public class VersionParserSAX {
//	
//	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
//    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
//    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
//
//	/**
//	 * 
//	 */
//	private XMLReader parser;
//	/**
//	 * The informations of the tag version found
//	 */
//	private final ArrayList<VersionApk> versionsApk;
//	/**
//	 * The location of the package 
//	 */
//	private final StringBuilder pkg;
//	
//	
//	/**
//	 * 
//	 * @param sourcePath
//	 * @param source
//	 * @param schemaSource
//	 * @throws IOException
//	 * @throws SAXException
//	 */
//	public VersionParserSAX(String sourcePath, ResourceSource source, String schemaSource) throws IOException, SAXException {
//		
//		parser = getParser();
//		parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//		parser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));
//		versionsApk = new ArrayList<VersionApk>();
//		
//		pkg = new StringBuilder();
//		parser.setContentHandler( new VersionContentHandler(pkg,versionsApk) );
//		switch(source){
//			case WEB: parser.parse(sourcePath); break;
//			case FILE: parser.parse(new InputSource(sourcePath));break;
//			default: throw new IllegalArgumentException("Please give a valid source to get the given xml file "+sourcePath);
//		}
//		
//		//this(getParser(),sourcePath,source);
//		//parser.setErrorHandler(handler);
//		
//	}
//	
//	/**
//	 * 
//	 * @return
//	 * @throws SAXException
//	 */
//	private static XMLReader getParser() throws SAXException{ return XMLReaderFactory.createXMLReader(); }
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public ArrayList<VersionApk> getVersionsApk(){
//		return versionsApk;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public String getPkg(){
//		return pkg.toString();
//	}
//	
//}
