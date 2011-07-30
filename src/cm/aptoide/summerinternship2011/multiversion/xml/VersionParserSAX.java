/**
 * 
 */
package cm.aptoide.summerinternship2011.multiversion.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import cm.aptoide.summerinternship2011.ResourceSource;
import cm.aptoide.summerinternship2011.multiversion.VersionApk;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Creates a xml version data parser reader more efficient than the VersionParserDOM.java even thought less features are supported.
 * 
 */
public class VersionParserSAX {
	
	private XMLReader parser;
	/**
	 * The informations of the tag version found
	 */
	private ArrayList<VersionApk> versionsApk;
	/**
	 * The location of the package 
	 */
	private String pkg;
	/**
	 * null if none any element started being read 
	 */
	private VersionFileElement versionData;
	/**
	 * null if none version started being read 
	 */
	private VersionFileElement appChild;
	
	private String versionnumber;
	private String uri;
	private String md5;
	
	/**
	 * 
	 * @param sourcePath
	 * @param source
	 * @throws IOException
	 * @throws SAXException
	 */
	public VersionParserSAX(String sourcePath, ResourceSource source) throws IOException, SAXException {
		parser = XMLReaderFactory.createXMLReader();
		VersionContentHandler handler = new VersionContentHandler();
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);
		versionsApk = new ArrayList<VersionApk>();
		switch(source){
			case WEB: parser.parse(sourcePath);break;
			case FILE: parser.parse(new InputSource(sourcePath));break;
			default: throw new IllegalArgumentException("Please give a valid source to get the given xml file "+sourcePath);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<VersionApk> getVersionsApk(){
		return versionsApk;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPkg(){
		return pkg;
	}
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 * The default handler for the SAX reader. 
	 * 
	 */
	private class VersionContentHandler extends DefaultHandler{
		 
		/**
		  * Handle the start of an element.
		  */
		  public void startElement (String uri, String name,String qName, Attributes atts)
		  {
			  VersionFileElement element = VersionFileElement.valueOfToUpper(name);
			  switch(VersionFileElement.valueOfToUpper(name)){
			  	case PKG: 
			  	case VERSION: appChild=element;break;
			  	case VERSIONNUMBER: 
			  	case URI:
			  	case MD5: versionData=element;
			  	default: break;
			  }
		  }
		  
		  /**
		   * Handle the end of an element.
		   */
		  public void endElement (String uri, String name, String qName)
		  {
			  switch(VersionFileElement.valueOfToUpper(name)){
			  	case VERSION: 
				  	try {
						versionsApk.add(new VersionApk(versionnumber,VersionParserSAX.this.uri,md5));
					} catch (MalformedURLException e) { e.printStackTrace(); }
			  	case PKG: 
			  		appChild=null;break;
			  	case VERSIONNUMBER: 
			  	case URI:
			  	case MD5: 
			  		versionData=null;
			  	default: break;
			  }
		  }
		
		  /**
		   * Handle character data.
		   */
		  public void characters (char ch[], int start, int length)
		  {
			  
			  if(versionData!=null && appChild!=null && appChild.equals(VersionFileElement.VERSION)){
				  switch(versionData){
				  	case VERSIONNUMBER: versionnumber = new String(ch, start, length);  
				  	case URI: uri = new String(ch, start, length); break; 
				  	case MD5: md5 = new String(ch, start, length); break; 
				  	default: break;
				  }
			  } else if(appChild!=null && appChild.equals(VersionFileElement.PKG) ){
				  pkg =  new String(ch, start, length);
			  }
		  }
	}
	
}
