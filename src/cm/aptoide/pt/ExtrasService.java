/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExtrasService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private enum Enum {
		APKID,CMT,DELTA,PKG, EXTRAS
	}
	private static ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {

			Thread t = new Thread(r);
			t.setPriority(1);

			return t;
		}
	});
	private static ArrayList<String> parsingList = new ArrayList<String>();
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@SuppressLint("NewApi")
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		onStartCommand(intent, START_NOT_STICKY, startId);
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onStart");
		String path = "";
		try{
			path = ((ArrayList<String>) intent.getSerializableExtra("path")).get(0);
		}catch (Exception e){
			e.printStackTrace();
		}


		if(!parsingList.contains(path)){
			parsingList.add(path);
			File xml = new File(path);
//			String md5 = Md5Handler.md5Calc(xml);
			executor.submit(new ExtrasParser(xml,getApplicationContext(),""));
			System.out.println("Extras starting");
		}

		return START_NOT_STICKY;
	}

	public class ExtrasParser extends Thread{
		File xml;
		private Context context;
		private String md5;



		public ExtrasParser(File xml, Context context,String md5) {
			this.xml=xml;
			this.context=context;
			this.md5 = md5;
		}

		public void run(){
			try{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				parser.parse(xml, new ExtrasHandler(context,md5));
			}catch(Exception e){
				xml.delete();
				e.printStackTrace();
				parsingList.remove(xml.getAbsolutePath());
			}finally{
				xml.delete();
				parsingList.remove(xml.getAbsolutePath());
			}

		}
	}


	DefaultHandler handler = new DefaultHandler(){

		StringBuilder sb = new StringBuilder();
		String apkid;
		String cmt;
		private ContentValues value;
		private ContentValues[] value2 = new ContentValues[0];
		private ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		private int i = 0;

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch (Enum.valueOf(localName.toUpperCase())) {
			case PKG:
				value=new ContentValues();
				break;
			}
			sb.setLength(0);
		};
		public void startDocument() throws SAXException {
		};
		public void characters(char[] ch, int start, int length) throws SAXException {
			sb.append(ch,start,length);
		};

		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (Enum.valueOf(localName.toUpperCase())) {
			case APKID:
				apkid=sb.toString();
				break;
			case CMT:
				cmt=Html.fromHtml(sb.toString().replace("\n","<br>")).toString();
				break;
			case DELTA:

				break;
			case PKG:
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_APKID, apkid);
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_COMMENT, cmt);
				values.add(value);
				i++;
				if(i%100==0){
					getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
					values.clear();
				}

//				getContentResolver().insert(ExtrasContentProvider.CONTENT_URI, value);
//				dbhandler.addComment(apkid,cmt);
				apkid="";
				cmt="";
				break;
			case EXTRAS:
				break;
			default:
				break;
			}
		};
		public void endDocument() throws SAXException {
			if(values.size()>0){
				getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
				values.clear();
			}

			System.out.println("Extras ended.");
		};
	};

}
