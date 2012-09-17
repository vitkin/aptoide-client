package cm.aptoide.pt2;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class DynamicRepoParser {
	static ExecutorService executor = Executors.newFixedThreadPool(1);
	static Database db;
	static DynamicRepoParser parser;
	
	private DynamicRepoParser(Database db) {
		this.db=db;
	}
	
	public static DynamicRepoParser getInstance(Database db){
		if(parser==null){
			return new DynamicRepoParser(db);
		}else{
			return parser;
		}
	}
	
	public void parse(File xml, Server server, Category category){
		try {
			executor.submit(new Parser(server,xml,category)).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public class Parser extends Thread{ 
		Server server;
		File xml;
		Category category;
		
		public Parser(Server server, File xml, Category category) {
			this.server = server;
			this.xml=xml;
			server.xml=xml;
			this.category=category;
		}

		public void run(){
			try{
				setPriority(MIN_PRIORITY);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				System.out.println("DynamicParsing repo_id:" + server.id);
				parser.parse(xml, new DynamicRepoParserHandler(db,server,category));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
//				xml.delete();
			}

		}
	}
}
