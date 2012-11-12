package cm.aptoide.pt2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cm.aptoide.pt2.Server.State;
import cm.aptoide.pt2.util.Md5Handler;
import cm.aptoide.pt2.views.ViewApk;


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
		
		elements.put("date", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setDate(sb.toString());
			}
		});
		
		elements.put("del", new ElementHandler() {


			public void startElement(Attributes atts) throws SAXException {
				isRemove = true;
			}

			@Override
			public void endElement() throws SAXException {
				isRemove = true;
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
				server.screenspath=sb.toString();
			}
		});

		elements.put("webservicespath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.webservicesPath=sb.toString();
			}
		});

		elements.put("apkpath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.apkPath=sb.toString();
			}
		});

		elements.put("package", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
			}

			@Override
			public void endElement() throws SAXException {
				if(isRemove){
					db.remove(apk,server);
					isRemove=false;
				}else{
					db.insert(apk);
				}
				
			}
		});

		elements.put("name", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setName(sb.toString());
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

		elements.put("ver", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setVername(sb.toString());
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

		elements.put("apkid", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
				apk.setApkid(sb.toString());
				
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

		elements.put("md5h", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMd5(sb.toString());
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

		elements.put("catg", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setCategory1(sb.toString());
			}
		});

		elements.put("catg2", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setCategory2(sb.toString());
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

		elements.put("age", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setAge(Filters.Ages.lookup(sb.toString()).ordinal());
			}
		});

		elements.put("minSdk", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMinSdk(sb.toString());
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
				apk.setMinScreen(Filters.Screens.lookup(sb.toString()).ordinal());
			}
		});
		
		elements.put("minGles", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMinGlEs(sb.toString());
			}

		});

	}

	private String path;
	
	public RepoParserHandler(Database db, Server server, String path) {
		RepoParserHandler.db = db;
		RepoParserHandler.server = server;
		this.path = path;
	}
	
	static StringBuilder sb = new StringBuilder();
	static ViewApk apk = new ViewApk();
	static Database db;
	static Server server;
	
	private static boolean isRemove = false;
	
	long start;
	private static boolean delta = false;
	
	public void startDocument() throws SAXException {
		start = System.currentTimeMillis();
		db.prepare();
		server.state=State.PARSING;
		db.updateStatus(server);
		db.startTransation();
		apk.setRepo_id(server.id);
		delta = false;
		
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
			System.out.println("Writing delta");
			server.delta = Md5Handler.md5Calc(new File(path));
			System.out.println("Delta is:" +server.delta);
		}
		db.updateStatus(server);
		db.endTransation(server);
	}
	
}