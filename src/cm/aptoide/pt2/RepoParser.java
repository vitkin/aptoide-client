package cm.aptoide.pt2;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class RepoParser {
	
	static ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			
			Thread t = new Thread(r);
			
			t.setPriority(2);
			
			return t;
		}
	});
	
	static ExecutorService extrasExecutor = Executors.newFixedThreadPool(1);
	static Database db;
	static RepoParser parser;
	
	private RepoParser(Database db) {
		this.db=db;
		System.out.println("New Parser");
	}
	
	public static RepoParser getInstance(Database db){
		if(parser==null){
			return new RepoParser(db);
		}else{
			return parser;
		}
	}
	
	public void parse2(File xml, Server server, Category category){
		executor.submit(new TopParser(server,xml,category));
	}
	
	public void parse(String xml, Server server){
		executor.submit(new Parser(server,xml));
	}
	
	public class Parser extends Thread{ 
		Server server;
		String xml;
		
		public Parser(Server server, String xml) {
			this.server = server;
			this.xml=xml;
		}

		public void run(){
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				System.out.println("Parsing repo_id:" + server.id);
				parser.parse(new File(xml), new RepoParserHandler(db,server,xml));
			}catch(Exception e){
				db.endTransation(server);
				e.printStackTrace();
			}finally{
				new File(xml).delete();
			}

		}
	}
	
	
	
	public class TopParser extends Thread{ 
		Server server;
		File xml;
		Category category;
		
		public TopParser(Server server, File xml, Category category) {
			this.server = server;
			this.xml=xml;
			server.xml=xml;
			this.category=category;
		}

		public void run(){
			try{
				setPriority(Thread.MIN_PRIORITY);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				System.out.println("DynamicParsing repo_id:" + server.id);
				parser.parse(xml, new TopRepoParserHandler(db,server,category,false));
			}catch(Exception e){
				db.endTransation(server);
				e.printStackTrace();
			}finally{
				xml.delete();
			}

		}
	}
	
}
