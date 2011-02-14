/*
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class RssHandler extends DefaultHandler{
	
	private ApkNodeFull tmp_apk = new ApkNodeFull();
	
	Context mctx;
	String mserver;
	
	private int napk = 0;
	
	private int readed = 0;
	
	private boolean new_apk = false;
	private boolean apk_name = false;
	private boolean apk_path = false;
	private boolean apk_ver = false;
	private boolean apk_vercode = false;
	private boolean apk_id = false;
	private boolean apk_icon = false;
	private boolean apk_date = false;
	private boolean apk_rat = false;
	private boolean apk_md5hash = false;
	private boolean apk_down = false;
	private boolean apk_ctg = false;
	private boolean apk_ctg2 = false;
	
	private DbHandler db = null;

	private Vector<ApkNode> listapks= null;
	
	private Vector<IconNode> iconFetchList = new Vector<IconNode>();
	private Vector<IconNode> iconFinalFetchList = new Vector<IconNode>();
	
	private boolean iconsInPool = true;
	
	//private boolean requireLogin = false;
	private String usern = null;
	private String passwd = null;
		
	public RssHandler(Context ctx, String srv){
		mctx = ctx;
		mserver = srv;
		db = new DbHandler(mctx);
		listapks = db.getForUpdate();
		tmp_apk.name = "unknown";
		tmp_apk.ver = "0.0";
		tmp_apk.vercode = 0;
		tmp_apk.rat = 3.0f;
		tmp_apk.down = 0;
		tmp_apk.date = "2000-01-01";
		tmp_apk.md5hash = null;
		tmp_apk.catg="";
		tmp_apk.catg_type = 2;
		
	}
	
	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		super.characters(ch, start, length);
		if(apk_name){
			tmp_apk.name = new String(ch).substring(start, start + length);
		}else if(apk_id){
			tmp_apk.apkid = new String(ch).substring(start, start + length);
		}else if(apk_path){
			tmp_apk.path = new String(ch).substring(start, start + length);
		}else if(apk_ver){
			tmp_apk.ver = new String(ch).substring(start, start + length);
		}else if (apk_vercode){
			try{
				tmp_apk.vercode = new Integer(new String(ch).substring(start, start + length));
			}catch(Exception e){
				tmp_apk.vercode = 0;
			}
		}else if(apk_icon){
			IconNode a = new IconNode(new String(ch).substring(start, start + length), tmp_apk.apkid);
			synchronized(iconFetchList) {
				iconFetchList.add(a);
			}
		}else if(apk_date){
			tmp_apk.date = new String(ch).substring(start, start + length);
		}else if(apk_rat){
			try{
				tmp_apk.rat = new Float(new String(ch).substring(start, start + length));
			}catch(Exception e){
				tmp_apk.rat = 3.0f;
			}
		}else if(apk_md5hash){
			tmp_apk.md5hash = new String(ch).substring(start, start + length);
		}else if(apk_down){
			tmp_apk.down = new Integer(new String(ch).substring(start, start + length));
		}else if(apk_ctg){
			String tmp = new String(ch).substring(start, start + length);
			if(tmp.equals("Applications")){
				Log.d("Aptoide", "Aplications");
				tmp_apk.catg_type = 1;
			}else if(tmp.equals("Games")){
				tmp_apk.catg_type = 0;
				Log.d("Aptoide", "Games");
			}else
				tmp_apk.catg_type = 2;
		}else if(apk_ctg2){
			tmp_apk.catg = tmp_apk.catg.concat(new String(ch).substring(start, start + length));
			//String tmp = new String(ch).substring(start, start + length);
			//Log.d("Aptoide","======== " + tmp);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if(localName.trim().equals("package")){
			napk++;
			new_apk = false;
			
			if(tmp_apk.name.equalsIgnoreCase("Unknown"))
				tmp_apk.name = tmp_apk.apkid;
						
			ApkNode node = new ApkNode(tmp_apk.apkid, tmp_apk.vercode);
			if(!listapks.contains(node)){
				db.insertApk(false,tmp_apk.name, tmp_apk.path, tmp_apk.ver, tmp_apk.vercode,tmp_apk.apkid, tmp_apk.date, tmp_apk.rat, mserver, tmp_apk.md5hash, tmp_apk.down, tmp_apk.catg, tmp_apk.catg_type);
				//tmp_apk.isnew = false;
				//updateTable.add(tmp_apk);
				listapks.add(node);
			}else{
				int pos = listapks.indexOf(node);
				ApkNode list = listapks.get(pos);
				if(list.vercode < node.vercode){
					db.insertApk(true,tmp_apk.name, tmp_apk.path, tmp_apk.ver, tmp_apk.vercode,tmp_apk.apkid, tmp_apk.date, tmp_apk.rat, mserver, tmp_apk.md5hash, tmp_apk.down, tmp_apk.catg, tmp_apk.catg_type);
					//tmp_apk.isnew = true;
					//updateTable.remove(new ApkNodeFull(list.apkid));
					//updateTable.add(tmp_apk);
					listapks.remove(pos);
					listapks.add(node);
				}
			}
			
			readed++;
			if(readed >= 10){
				readed = 0;
				cleanTransHeap();
			}
			
			tmp_apk.name = "Unknown";
			tmp_apk.ver = "0.0";
			tmp_apk.vercode = 0;
			tmp_apk.rat = 3.0f;
			tmp_apk.date = "2000-01-01";
			tmp_apk.down = 0;
			tmp_apk.md5hash = null;
			tmp_apk.catg="";
			tmp_apk.catg_type = 2;
		}else if(localName.trim().equals("name")){
			apk_name = false;
		}else if(localName.trim().equals("path")){
			apk_path = false;
		}else if(localName.trim().equals("ver")){
			apk_ver = false;
		}else if(localName.trim().equals("vercode")){
			apk_vercode = false;
		}else if(localName.trim().equals("apkid")){
			apk_id = false;
		}else if(localName.trim().equals("icon")){
			apk_icon = false;
		}else if(localName.trim().equals("date")){
			apk_date = false;
		}else if(localName.trim().equals("rat")){
			apk_rat = false;
		}else if(localName.trim().equals("md5h")){
			apk_md5hash = false;
		}else if(localName.trim().equals("dwn")){
			apk_down = false;
		}else if(localName.trim().equals("catg")){
			apk_ctg = false;
		}else if(localName.trim().equals("catg2")){
			apk_ctg2 = false;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(localName.trim().equals("package")){
			new_apk = true;
		}else if(localName.trim().equals("name")){
			apk_name = true;
		}else if(localName.trim().equals("path")){
			apk_path = true;
		}else if(localName.trim().equals("ver")){
			apk_ver = true;
		}else if(localName.trim().equals("vercode")){
			apk_vercode = true;
		}else if(localName.trim().equals("apkid")){
			apk_id = true;
		}else if(localName.trim().equals("icon")){
			apk_icon = true;
		}else if(localName.trim().equals("date")){
			apk_date = true;
		}else if(localName.trim().equals("rat")){
			apk_rat = true;
		}else if(localName.trim().equals("md5h")){
			apk_md5hash = true;
		}else if(localName.trim().equals("dwn")){
			apk_down = true;
		}else if(localName.trim().equals("catg")){
			apk_ctg = true;
		}else if(localName.trim().equals("catg2")){
			apk_ctg2 = true;
		}
	}
	
	
	
	
	@Override
	public void startDocument() throws SAXException {
		String[] logins = null; 
		logins = db.getLogin(mserver);
		if(logins != null){
			//requireLogin = true;
			usern = logins[0];
			passwd = logins[1];
		}
		new Thread() {
			public void run() {
				try{
					while(iconsInPool){
						while(!iconFetchList.isEmpty()){
							IconNode node = null;
							synchronized(iconFetchList){
								node = iconFetchList.remove(0);
							}
							String test_file = mctx.getString(R.string.icons_path) + node.name;
							
							File exists = new File(test_file);
							if(exists.exists()){
							}else {
								synchronized(iconFinalFetchList){
									iconFinalFetchList.add(node);
								}

							}
						}
						Thread.sleep(1000);
					}
				} catch (Exception e) { 
				}
			}
		}.start();
		
		new Thread() {
			public void run() {
				IconNode node = null;
				try{
					while(true){
						Thread.sleep(2000);
						if(iconFinalFetchList.isEmpty() || (!iconsInPool)){
							break;
						}else{
							synchronized(iconFinalFetchList){
								node = iconFinalFetchList.remove(0);
							}
							getIcon(node.url, node.name);
						}
					}
				}catch (Exception e){
				}
			}
		}.start();
		
		db.startTrans();
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		Log.d("Aptoide","Done parsing XML from " + mserver + " ...");
		db.updateServerNApk(mserver, napk);
		db.endTrans();
		new Thread() {
			public void run() {
				try{
					
					while(true) {
						if(iconFetchList.size() == 0){
							iconsInPool = false;
							break;
						}
						Thread.sleep(2000);
					}

					Thread main_icon_thread = new Thread(new FetchIcons(), "T1");

					main_icon_thread.start();						
					
				} catch (Exception e) {  }
			}
		}.start();
		
		IconNode node = null;
		try{
			while(true){
				if(iconFinalFetchList.isEmpty()){
					break;
				}else{
					synchronized(iconFinalFetchList){
						node = iconFinalFetchList.remove(0);
					}
					getIcon(node.url, node.name);
				}
			}
		}catch (Exception e){
		}
		super.endDocument();
	}
	
	private void cleanTransHeap(){
		db.endTrans();
		db.startTrans();
	}

	private void getIcon(String uri, String name){
		String url = mserver + "/" + uri;
		String file = mctx.getString(R.string.icons_path) + name;
		
		try {
			FileOutputStream saveit = new FileOutputStream(file);
			
			HttpResponse mHttpResponse = NetworkApis.getHttpResponse(url, usern, passwd, mctx);
			
			if(mHttpResponse.getStatusLine().getStatusCode() == 401){
				return;
			}else if(mHttpResponse.getStatusLine().getStatusCode() == 403){
				return;
			}else{
				InputStream getit = mHttpResponse.getEntity().getContent();
				byte data[] = new byte[8096];
				int readed;
				while((readed = getit.read(data, 0, 8096)) != -1) {
					saveit.write(data,0,readed);
				}
			}
			
		}catch (IOException e) { }
		catch (IllegalArgumentException e) { }

	}
	
	private class FetchIcons implements Runnable {
		public FetchIcons() {	}
		
		public void run() {
			IconNode node = null;
			try{
				while(true){
					if(iconFinalFetchList.isEmpty()){
						break;
					}else{
						synchronized(iconFinalFetchList){
							node = iconFinalFetchList.remove(0);
						}
						getIcon(node.url, node.name);
					}
				}

			}catch (Exception e){
			}
		}
	}

}
