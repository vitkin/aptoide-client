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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class RssHandler extends DefaultHandler{
	
	private class Apk {
		private String name;
		private String apkid;
		private String path;
		private String ver;
		private int vercode;
		private String date;
		private float rat;
		private String md5hash;
	}
	
	private Apk tmp_apk = new Apk();
	
	Context mctx;
	String mserver;
	
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
	
	private DbHandler db = null;

	
	public RssHandler(Context ctx, String srv){
		mctx = ctx;
		mserver = srv;
		db = new DbHandler(mctx);
		tmp_apk.name = "unknown";
		tmp_apk.ver = "0.0";
		tmp_apk.vercode = 0;
		tmp_apk.rat = 3.0f;
		tmp_apk.date = "2000-01-01";
		tmp_apk.md5hash = null;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
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
			File icn = new File(mctx.getString(R.string.icons_path)+tmp_apk.apkid);
			if(!icn.exists()){
				getIcon(new String(ch).substring(start, start + length));
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
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(localName.trim().equals("package")){
			new_apk = false;
			if(tmp_apk.name.equalsIgnoreCase("Unknown"))
				tmp_apk.name = tmp_apk.apkid;
			db.insertApk(tmp_apk.name, tmp_apk.path, tmp_apk.ver, tmp_apk.vercode,tmp_apk.apkid, tmp_apk.date, tmp_apk.rat, mserver, tmp_apk.md5hash);
			tmp_apk.name = "Unknown";
			tmp_apk.ver = "0.0";
			tmp_apk.vercode = 0;
			tmp_apk.rat = 3.0f;
			tmp_apk.date = "2000-01-01";
			tmp_apk.md5hash = null;
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
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
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
		}
	}
	
	private void getIcon(String uri){
		try{
			BufferedInputStream getit = new BufferedInputStream(new URL(mserver+"/"+uri).openStream());
			File file_teste = new File(mctx.getString(R.string.icons_path) + tmp_apk.apkid);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(mctx.getString(R.string.icons_path) + tmp_apk.apkid);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			bout.close();
			getit.close();
			saveit.close();
		} catch(Exception e){
			
		}
		//return(mctx.getString(R.string.icons_path) + tmp_apk.apkid);
	}

}
