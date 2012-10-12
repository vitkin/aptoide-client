package cm.aptoide.pt2;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.views.ViewApk;

public class ItemBasedApkHandler extends DefaultHandler {
	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}
	
	private static ViewApk parent_apk;
	
	public ItemBasedApkHandler(Database db, ViewApk parent_apk) {
		ItemBasedApkHandler.parent_apk=parent_apk;
		ItemBasedApkHandler.db=db;
	}
	private static Database db;
	final static Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	final static ViewApk apk = new ViewApk();
	final static Server server = new Server();
	final static StringBuilder sb  = new StringBuilder();	
	static {
		elements.put("apkid", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setApkid(sb.toString());
			}
		});
		
		elements.put("ver", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setVername(sb.toString());
			}
		});
		
		elements.put("catg2", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setCategory2(sb.toString());
			}
		});
		
		elements.put("dwn", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setDownloads(sb.toString());
			}
		});
		
		elements.put("rat", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setRating(sb.toString());
			}
		});
		
		elements.put("path", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setPath(sb.toString());
			}
		});
		
		elements.put("sz", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setSize(sb.toString());
			}
		});
		
		elements.put("vercode", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setVercode(Integer.parseInt(sb.toString()));
			}
		});
		
		elements.put("iconspath", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				server.iconsPath=sb.toString();
			}
		});
		
		elements.put("screenspath", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				server.screenspath=sb.toString();
			}
		});
		
		elements.put("basepath", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				server.basePath=sb.toString();
			}
		});
		
		elements.put("name", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				if(insidePackage){
					apk.setName(sb.toString());
				}else{
					server.url=sb.toString();
				}
				
			}
		});
		
		elements.put("icon", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				apk.setIconPath(sb.toString());
			}
		});
		
		elements.put("repository", new ElementHandler() {
			
			@Override
			public void startElement(Attributes atts) throws SAXException {
				
			}
			
			@Override
			public void endElement() throws SAXException {
				System.out.println("itembased");
				try{
					db.insertItemBasedApk(server,apk,parent_apk.getApkid());
				}catch (Exception e){
					e.printStackTrace();
				}
				
			}
		});
		elements.put("package", new ElementHandler() {
			
			

			@Override
			public void startElement(Attributes atts) throws SAXException {
				insidePackage = true;
			}
			
			@Override
			public void endElement() throws SAXException {
				insidePackage = false;
			}
		});
		
		
	}
	private static boolean insidePackage;
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		try {
			elements.get(localName).startElement(attributes);
		} catch (Exception e) {
			System.out.println("Element not found: " + localName);
		}
		sb.setLength(0);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		try {
			elements.get(localName).endElement();
		} catch (Exception e) {
			System.out.println("Element not found: " + localName);
		}
	}
	
}
