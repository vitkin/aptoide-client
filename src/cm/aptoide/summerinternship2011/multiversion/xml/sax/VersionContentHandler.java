/**
 * 
 */
package cm.aptoide.summerinternship2011.multiversion.xml.sax;

import java.net.MalformedURLException;
import java.util.ArrayList;
import org.xml.sax.Attributes;

import org.xml.sax.helpers.DefaultHandler;


import cm.aptoide.summerinternship2011.multiversion.VersionApk;
import cm.aptoide.summerinternship2011.multiversion.xml.VersionFileElement;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * The default handler for the SAX reader. 
 * 
 */
public class VersionContentHandler extends DefaultHandler{
	
	/**
	 * null if none any element started being read 
	 */
	private VersionFileElement versionData;
	/**
	 * null if none version started being read 
	 */
	private VersionFileElement appChild;
	
	private StringBuilder pkg;
	private ArrayList<VersionApk> versionsApk;
	private String versionnumber;
	private String uri;
	private String md5;
	
	/**
	 * 
	 * @param pkg
	 * @param versionsApk
	 */
	public VersionContentHandler(StringBuilder pkg, ArrayList<VersionApk> versionsApk) {
		versionnumber = null;
		uri = null;
		md5 = null;
		appChild = null;
		versionData = null;
		this.pkg = pkg;
		this.versionsApk = versionsApk;
		
	}
	
	/**
	  * Handle the start of an element.
	  */
	  public void startElement (String uri, String name, String qName, Attributes atts){
		  
		  //System.out.println("Start "+qName); 
		  VersionFileElement element = VersionFileElement.valueOfToUpper(name);
		  switch(element){
		  	case PKG: 
		  	case VERSION: appChild=element;break;
		  	case VERSION_NUMBER: 
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
		  //System.out.println("End "+qName);
		  switch(VersionFileElement.valueOfToUpper(name)){
		  	case VERSION: 
			  	try {
					versionsApk.add(new VersionApk(versionnumber,this.uri,md5));
				} catch (MalformedURLException e) {}
		  	case PKG: 
		  		appChild=null;break;
		  	case VERSION_NUMBER: 
		  	case URI:
		  	case MD5: 
		  		versionData=null;
		  	case NOT_VALID:
		  	default: break;
		  }
		  
	  }
	
	  /**
	   * Handle character data.
	   */
	  public void characters (char ch[], int start, int length)
	  {
		  //System.out.println("Characters");
		  if(versionData!=null && appChild!=null && appChild.equals(VersionFileElement.VERSION)){
			  switch(versionData){
			  	case VERSION_NUMBER: versionnumber = new String(ch, start, length);  
			  	case URI: uri = new String(ch, start, length); break; 
			  	case MD5: md5 = new String(ch, start, length); break; 
			  	default: break;
			  }
		  } else if(appChild!=null && appChild.equals(VersionFileElement.PKG) ){
			  pkg.append(new String(ch, start, length));
		  }
	  }

//	/* (non-Javadoc)
//	 * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
//	 */
//	@Override
//	public void error(SAXParseException e) throws SAXException {
//		// TODO Auto-generated method stub
//		super.error(e);
//		throw e;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
//	 */
//	@Override
//	public void warning(SAXParseException e) throws SAXException {
//		// TODO Auto-generated method stub
//		super.warning(e);
//		throw e;
//	}
	  
	  
}