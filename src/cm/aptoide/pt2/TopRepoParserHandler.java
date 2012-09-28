package cm.aptoide.pt2;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.RepoParserHandler.ElementHandler;
import cm.aptoide.pt2.views.ViewApk;


public class TopRepoParserHandler extends DefaultHandler {
	
	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}
	
	final static Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	final static ViewApk apk = new ViewApk();
	final static StringBuilder sb  = new StringBuilder();
	static {
		elements.put("name", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setName(sb.toString());
			}
		});
		
		elements.put("iconspath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
//				server.iconsPath=sb.toString();
			}
		});
		
		elements.put("repository", new ElementHandler() {
			

			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
				if(!db.getTopAppsHash(server.id).equals(server.delta)){
					db.deleteTopApps(server.id);
				}else{
					db.endTransation(server);
					throw new SAXException();
				}
				db.insertTopServerInfo(server);
			}
		});
		
		elements.put("hash", new ElementHandler() {
			

			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.delta=sb.toString();
			}
		});
		
		elements.put("package", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
				
			}

			@Override
			public void endElement() throws SAXException {
				db.insertTop(apk,category);
				System.out.println("Insert");
			}
		});
		
		elements.put("ver", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setVername(sb.toString());
			}
		});
		
		elements.put("apkid", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setApkid(sb.toString());
			}
		});
		
		elements.put("vercode", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setVercode(Integer.parseInt(sb.toString()));
			}
		});
		
		elements.put("sz", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setSize(sb.toString());
			}
		});
		
		elements.put("screen", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
			}
		});
		
		elements.put("icon", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setIconPath(sb.toString());
			}
		});
		
		elements.put("dwn", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setDownloads(sb.toString());
			}
		});
		
		elements.put("rat", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setRating(sb.toString());
			}
		});
		
		elements.put("path", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setPath(sb.toString());
			}
		});
		
		
	}
	
	private static Database db;
	private static Server server;
	private static Category category;
	
	public TopRepoParserHandler(Database db, Server server,Category category) {
		TopRepoParserHandler.server = server;
		TopRepoParserHandler.db = db;
		TopRepoParserHandler.category=category;
	}
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		db.prepare();
		db.startTransation();
		System.out.println(server.id);
		apk.setRepo_id(server.id);
	}
	@Override
	public void startElement(String uri, String localName,
			String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		sb.setLength(0);
		ElementHandler elementHandler = elements.get(localName);
		 
		if (elementHandler != null) {
			elementHandler.startElement(attributes);
		} else {
			System.out.println("Element not found:" + localName);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch,start,length);
	}

	@Override
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		ElementHandler elementHandler = elements.get(localName);
		 
		if (elementHandler != null) {
			elementHandler.endElement();
		} else {
			System.out.println("Element not found:" + localName);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		db.endTransation(server);
	}
	
	
}
