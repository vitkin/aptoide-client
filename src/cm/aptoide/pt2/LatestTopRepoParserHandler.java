/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.pm.FeatureInfo;

import cm.aptoide.pt2.RepoParserHandler.ElementHandler;
import cm.aptoide.pt2.views.ViewApk;


public class LatestTopRepoParserHandler extends DefaultHandler {
	
	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}
	
	final static Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	final static ViewApk apk = new ViewApk();
	final static StringBuilder sb  = new StringBuilder();
	private static boolean insidePackage = false;
	static {
		elements.put("name", new ElementHandler() {
			

			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				if(insidePackage){
					apk.setName(sb.toString());
				}else{
					server.name=sb.toString();
				}
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
		
		elements.put("repository", new ElementHandler() {
			

			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				
				if(!db.getTopAppsHash(server.id,category).equals(server.top_hash)){
					System.out.println("Deleting " +category.name() +"apps ");
					db.deleteTopApps(server.id,category);
				}else{
//					db.endTransation(server);
					System.out.println("NOT Deleting " +category.name() +"apps ");
					throw new SAXException();
				}
				db.insertTopServerInfo(server, category,featured);
			}
		});
		
		elements.put("hash", new ElementHandler() {
			

			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.top_hash=sb.toString();
			}
		});
		
		elements.put("package", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
				insidePackage = true;
				
			}

			@Override
			public void endElement() throws SAXException {
				apk.setId(db.insertTop(apk,category));
				db.insertScreenshots(apk,category);
				insidePackage = false;
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
				apk.addScreenshot(sb.toString());
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
		
		elements.put("md5h", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMd5(sb.toString());
			}
		});
		
		elements.put("basepath", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				server.basePath=sb.toString();
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
		
		elements.put("minSdk", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMinSdk(sb.toString());
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
		
		elements.put("minScreen", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMinScreen(Filters.Screens.lookup(sb.toString()).ordinal());
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
		
		elements.put("cmt", new ElementHandler() {
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				value = new ContentValues();
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_APKID, apk.getApkid());
				value.put(ExtrasDbOpenHelper.COLUMN_COMMENTS_COMMENT, sb.toString());
				values.add(value);
				i++;
				if(i%100==0){
					Database.context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
					values.clear();
				}
			}
		});
		
		
	}
	
	private static Database db;
	private static Server server;
	private static Category category;
	private static boolean featured;
	
	public LatestTopRepoParserHandler(Database db, Server server,Category category, boolean b) {
		LatestTopRepoParserHandler.server = server;
		LatestTopRepoParserHandler.db = db;
		LatestTopRepoParserHandler.category=category;
		featured = b;
	}
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		db.prepare();
		server.clear();
//		db.startTransation();
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
		if(values.size()>0){
			Database.context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
			values.clear();
		}
//		db.endTransation(server);
	}
	private static int i = 0;
	private static ContentValues value;
	private static ContentValues[] value2 = new ContentValues[0];
	private static ArrayList<ContentValues> values = new ArrayList<ContentValues>();
	
	
}
