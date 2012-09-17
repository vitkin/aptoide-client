package cm.aptoide.pt2;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.RepoParserHandler.ElementHandler;


public class DynamicRepoParserHandler extends DefaultHandler {
	
	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}
	
	final static Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	final static Apk apk = new Apk();
	final static StringBuilder sb  = new StringBuilder();
	static {
		elements.put("name", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.name=sb.toString();
			}
		});
		
		elements.put("iconspath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.iconsPath=sb.toString();
			}
		});
		
		elements.put("repository", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				db.insertTopServerInfo(server);
			}
		});
		
		elements.put("package", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
				
			}

			@Override
			public void endElement() throws SAXException {
				db.insertDynamic(apk,category);
			}
		});
		
		elements.put("ver", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.vername=sb.toString();
			}
		});
		
		elements.put("apkid", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.apkid=sb.toString();
			}
		});
		
		elements.put("vercode", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.vercode=sb.toString();
			}
		});
		
		elements.put("sz", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
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
				apk.iconPath=sb.toString();
			}
		});
		
		
	}
	
	private static Database db;
	private static Server server;
	private static Category category;

	public DynamicRepoParserHandler(Database db, Server server,Category category) {
		DynamicRepoParserHandler.server = server;
		DynamicRepoParserHandler.db = db;
		DynamicRepoParserHandler.category=category;
	}
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		System.out.println(server.id);
		apk.repo_id=server.id;
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
//			System.out.println("Element not found:" + localName);
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
//			System.out.println("Element not found:" + localName);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	
}
