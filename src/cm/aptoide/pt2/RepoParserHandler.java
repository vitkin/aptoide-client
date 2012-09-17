package cm.aptoide.pt2;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.Server.State;

import android.content.Context;
import android.util.Log;


public class RepoParserHandler extends DefaultHandler {
	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}
	final static Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	 
	static {
		
		elements.put("apklst", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("repository", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				if(!delta){
					db.deleteServer(server.id,false);
				}
				db.insertServerInfo(server);
				
			}
		});
		
		elements.put("del", new ElementHandler() {


			public void startElement(Attributes atts) throws SAXException {
				isRemove = true;
			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("basepath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.basePath = sb.toString();
			}
		});
		
		elements.put("appscount", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});
		
		elements.put("iconspath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.iconsPath = sb.toString();
			}
		});
		
		elements.put("screenspath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("webservicespath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("apkpath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("package", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
				
			}

			@Override
			public void endElement() throws SAXException {
				if(isRemove){
					db.remove(apk);
					isRemove=false;
				}else{
					
				}
				db.insert(apk);
			}
		});

		elements.put("name", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.name=sb.toString();
			}
		});

		elements.put("path", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
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

		elements.put("vercode", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.vercode=sb.toString();
				
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

		elements.put("icon", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.iconPath=sb.toString();
			}
		});

		elements.put("date", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("md5h", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("dwn", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.downloads=sb.toString();
			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("rat", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("catg", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.category1=sb.toString();
			}
		});

		elements.put("catg2", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.category2=sb.toString();
			}
		});

		elements.put("sz", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.size=sb.toString();
			}
		});

		elements.put("age", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});

		elements.put("minSdk", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});
		
		elements.put("delta", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				
			}

			@Override
			public void endElement() throws SAXException {
				delta = true;
				if(sb.toString().length()>0){
					server.delta = sb.toString();
				}
				
			}
		});

		elements.put("minScreen", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}
		});
		
		elements.put("minGles", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
			}

		});

	}
	
	public RepoParserHandler(Database db, Server server) {
		RepoParserHandler.db = db;
		RepoParserHandler.server = server;
	}
	
	static StringBuilder sb = new StringBuilder();
	static Apk apk = new Apk();
	static Database db;
	static Server server;
	
	private static boolean isRemove = false;
	
	long start;
	private int i = 0;
	private static boolean delta = false;
	
	public void startDocument() throws SAXException {
		start = System.currentTimeMillis();
		server.state=State.PARSING;
		db.updateStatus(server);
		db.startTransation();
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
		server.state = State.PARSED;
		if(!delta ){
			try {
				server.delta = Md5Handler.md5Calc(server.xml);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		db.updateStatus(server);
		db.endTransation(System.currentTimeMillis() - start,server);
	}
	
}