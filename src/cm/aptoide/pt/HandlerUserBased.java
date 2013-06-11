/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import android.content.ContentValues;
import cm.aptoide.pt.views.ViewApkUserBased;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HandlerUserBased extends DefaultHandler {

	Map<String, ElementHandler> elements = new HashMap<String, ElementHandler>();
	protected Database db = Database.getInstance();

	interface ElementHandler {
		void startElement(Attributes atts) throws SAXException;
		void endElement() throws SAXException;
	}

	protected ViewApkUserBased apk;
	StringBuilder sb  = new StringBuilder();

	public HandlerUserBased(){
		apk = new ViewApkUserBased();
		loadSpecificElements();
	}

	void loadSpecificElements() {

		elements.put("apklst", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

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
				apk.getServer().iconsPath=sb.toString();
			}
		});

		elements.put("screenspath", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.getServer().screenspath=sb.toString();
			}
		});

		elements.put("basepath", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.getServer().basePath=sb.toString();
			}
		});

		elements.put("md5h", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.setMd5(sb.toString());
			}
		});

		elements.put("screen", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.addScreenshot(sb.toString());
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

		elements.put("hash", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("appscount", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});


		elements.put("status", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("productscount", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("type", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("timestamp", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("likes", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("dislikes", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {

			}
		});

		elements.put("featuregraphicpath", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {

			}

			@Override
			public void endElement() throws SAXException {
				apk.getServer().featuredgraphicPath=sb.toString();
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
					apk.getServer().url=sb.toString();
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

		elements.put("package", new ElementHandler() {

			@Override
			public void startElement(Attributes atts) throws SAXException {
				apk.clear();
				insidePackage = true;
			}

			@Override
			public void endElement() throws SAXException {
				try{
					db.insert(apk);
				}catch (Exception e){
					e.printStackTrace();
				}
				insidePackage = false;

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
	private static boolean insidePackage;


	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if(values.size()>0){
					Database.context.getContentResolver().bulkInsert(ExtrasContentProvider.CONTENT_URI, values.toArray(value2));
					values.clear();
				}
			}
		}).start();
	}

	private static int i = 0;
	private static ContentValues value;
	private static ContentValues[] value2 = new ContentValues[0];
	private static ArrayList<ContentValues> values = new ArrayList<ContentValues>();

}
