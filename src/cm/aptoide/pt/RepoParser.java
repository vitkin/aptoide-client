/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import android.content.Intent;
import android.util.Log;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class RepoParser {
	static final Object lock = new Object();
	static ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {

			Thread t = new Thread(r);

			t.setPriority(3);

			return t;
		}
	});

	static Database db;
	static RepoParser parser;

	private RepoParser(Database db) {
		RepoParser.db=db;
		System.out.println("New Parser");
	}

	public static RepoParser getInstance(Database db){

			if(parser==null){
				synchronized (lock) {
					return new RepoParser(db);
				}
			}else{
				return parser;
			}

	}

	public void parseTop(String xml, Server server){
		Log.d("Parser", "Starting top.xml on serverid "+  server.id);
		executor.submit(new TopParser(server,xml,Category.TOP));
	}

	public void parseLatest(String xml, Server server){
		Log.d("Parser", "Starting latest.xml on serverid "+  server.id);
		executor.submit(new LatestParser(server,xml,Category.LATEST));
	}

	public void parseInfoXML(String xml, Server server){
		Log.d("Parser", "Starting info.xml on serverid "+  server.id);
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
			db.startTransation();
			server.state = cm.aptoide.pt.Server.State.PARSING;
			db.updateStatus(server);
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(new File(xml), new HandlerInfoXml(server,xml));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				new File(xml).delete();
			}
			server.state = cm.aptoide.pt.Server.State.PARSED;
			
			ApplicationAptoide.getContext().sendBroadcast(new Intent("parse_completed"));
			
			db.updateStatus(server);
			db.endTransation(server);

		}
	}



	public class TopParser extends Thread{
		Server server;
		File xml;
		Category category;

		public TopParser(Server server, String xml, Category category) {
			this.server = server;
			this.xml=new File(xml);
			server.xml=xml;
			this.category=category;
		}

		public void run(){
//			db.startTransation();
			server.state = Server.State.PARSINGTOP;
			db.updateStatus(server);
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(xml, new HandlerTop(server));

			}catch(Exception e){

			}finally{
				xml.delete();
                db.insertTopHash(server.id, server.hash);
            }
            server.state = cm.aptoide.pt.Server.State.PARSED;
            db.updateStatus(server);
//			db.updateStatus(server);
//			db.endTransation(server);
		}
	}

	public class LatestParser extends Thread{
		Server server;
		File xml;
		Category category;

		public LatestParser(Server server, String xml, Category category) {
			this.server = server;
			this.xml=new File(xml);
			server.xml=xml;
			this.category=category;
		}

		public void run(){
//			db.startTransation();
            server.state = Server.State.PARSINGLATEST;
            db.updateStatus(server);
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(xml, new HandlerLatest(server));
			}catch(Exception e){

			}finally{
				xml.delete();
                db.insertLatestHash(server.id, server.hash);
			}
            server.state = cm.aptoide.pt.Server.State.PARSED;
            db.updateStatus(server);

//			db.endTransation(server);
		}
	}

}
