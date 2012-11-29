/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import cm.aptoide.pt.Category;
import cm.aptoide.pt.Database;
import cm.aptoide.pt.ExtrasService;
import cm.aptoide.pt.RepoParser;
import cm.aptoide.pt.Server;
import cm.aptoide.pt.Server.State;
import cm.aptoide.pt.util.NetworkUtils;
import cm.aptoide.pt.util.RepoUtils;

public class MainService extends Service {
//	Database db;
	private static boolean isParsing = false;
	String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	String defaultXmlPath = defaultPath+"/.aptoide/info.xml";
	String defaultTopXmlPath = defaultPath+"/.aptoide/top.xml";
	String defaultLatestXmlPath = defaultPath+"/.aptoide/latest.xml";
	String defaultExtrasXmlPath = defaultPath+"/.aptoide/extras.xml";
	static ArrayList<String> serversParsing = new ArrayList<String>();
	@Override
	public IBinder onBind(Intent intent) {
		registerReceiver(receiver , new IntentFilter("complete"));
		return new LocalBinder();
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				serversParsing.remove(intent.getStringExtra("server"));
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
	
	public class LocalBinder extends Binder{
		public MainService getService(){
			return MainService.this;
		}
	}
	
//	public void parse(final Database db){
//		new Thread() {
//			
//
//			public void run() {
//				
//				ArrayList<Server> servers = ;
//				
//				try {
//					for(Server server : servers){
//						if(serversParsing.get((int)server.id)==null){
//							
//						}
//						
//					}
//					sendBroadcast(new Intent("starting"));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}
	
	public String get(Server server,String xmlpath,String what, boolean delta) throws MalformedURLException, IOException{
		getApplicationContext().sendBroadcast(new Intent("connecting"));
		String hash = "";
		if (delta&&server.delta != null) {
			hash = "?hash=" + server.delta;
		}
		String url = server.url + what + hash;
		NetworkUtils utils = new NetworkUtils();
		File f = new File(xmlpath);
			InputStream in = utils.getInputStream(new URL(url), server.getLogin().getUsername(),
					server.getLogin().getPassword(),getApplicationContext());
			int i = 0;
			while (f.exists()) {
				f = new File(xmlpath + i++);
			}
			FileOutputStream out = new FileOutputStream(f);

			byte[] buffer = new byte[1024];
			int len;
			getApplicationContext().sendBroadcast(new Intent("downloading"));
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
		
		
    	
		return f.getAbsolutePath();
    }
//	
//	public String getTop(Server server,String xmlpath) throws MalformedURLException, IOException{
//		File f = new File(xmlpath);
//			getApplicationContext().sendBroadcast(new Intent("connecting"));
//			String url = server.url + "top.xml";
//			System.out.println(url);
//	    	InputStream in = getInputStream(new URL(url),server.username,server.password);
//	    	
//	    	
//	    	int i = 0;
//	    	while(f.exists()){
//	    		f = new File(xmlpath+i++);
//	    	}
//			FileOutputStream out = new FileOutputStream(f);
//			
//			byte[] buffer = new byte[1024];
//			int len;
//			getApplicationContext().sendBroadcast(new Intent("downloading"));
//			while ((len = in.read(buffer)) != -1) {
//				out.write(buffer, 0, len);
//			}
//			out.close();
//		
//		return f.getAbsolutePath();
//    }
//	
//	public String getLatest(Server server,String xmlpath) throws MalformedURLException, IOException{
//		File f = new File(xmlpath);
//			getApplicationContext().sendBroadcast(new Intent("connecting"));
//			String url = server.url + "latest.xml";
//			System.out.println(url);
//	    	InputStream in = getInputStream(new URL(url),server.username,server.password);
//	    	
//	    	
//	    	int i = 0;
//	    	while(f.exists()){
//	    		f = new File(xmlpath+i++);
//	    	}
//			FileOutputStream out = new FileOutputStream(f);
//			
//			byte[] buffer = new byte[1024];
//			int len;
//			getApplicationContext().sendBroadcast(new Intent("downloading"));
//			while ((len = in.read(buffer)) != -1) {
//				out.write(buffer, 0, len);
//			}
//			out.close();
//		
//		return f.getAbsolutePath();
//    }

	public boolean isParsing() {
		return isParsing ;
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void addStore(Database db, String uri_str, String username, String password) {
		Server server = null;
		try{
			if(db.getServer(uri_str)!=null){
				return;
			}
			db.addStore(uri_str,username,password);
			server = db.getServer(uri_str);
			addStoreInfo(db, server);
			parseServer(db, server);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void parseServer(final Database db, final Server server) throws MalformedURLException, IOException {
		
		
		if(!serversParsing.contains(server.url)){
			server.state=State.QUEUED;
			db.updateStatus(server);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
						parseTop(db, server);
						parseLatest(db, server);
					try{
						parseInfoXml(db, server);
					}catch (Exception e){
						server.state=State.FAILED;
						db.updateStatus(server);
						getApplicationContext().sendBroadcast(new Intent("status"));
						serversParsing.remove(server.url);
						e.printStackTrace();
					}
				}
			}).start();
			
			serversParsing.add(server.url);	
		}
	}
	
	public boolean deleteStore(Database db, long id){
		if(!serversParsing.contains(db.getServer(id, false).url)){
			db.deleteServer(id,true);
			return true;
		}
		return false;
	}
	
	public void parseTop(final Database db, final Server server) {
		new Thread(new Runnable() {
			public void run() {
				String path2;
				try {
					//			serversParsing.put((int)server.id, server);
					path2 = get(server, defaultTopXmlPath, "top.xml", false);
					RepoParser.getInstance(db).parse2(path2, server);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public void parseExtras(final Server server){
		new Thread(new Runnable() {
			public void run() {
				String path;
				try {
					path = get(server, defaultExtrasXmlPath, "extras.xml", true);
					Intent service = new Intent(MainService.this, ExtrasService.class);
					ArrayList<String> array = new ArrayList<String>();
					array.add(path);
					service.putExtra("path", array);
					startService(service);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void parseLatest(final Database db, final Server server){
			new Thread(new Runnable() {

				public void run() {
					String path2 = null;
					//			serversParsing.put((int)server.id, server);
					try {
						path2 = get(server, defaultLatestXmlPath, "latest.xml", false);
						RepoParser.getInstance(db).parse3(path2, server);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}).start();

	}
	
	public void parseInfoXml(final Database db, final Server server) throws MalformedURLException, IOException{
		String path = null;
		path = get(server,defaultXmlPath,"info.xml",true);
		RepoParser.getInstance(db).parse(path,server);
		parseExtras(server);
	}

	public void addStoreInfo(Database db, Server server) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
					String.format("http://webservices.aptoide.com/webservices/getRepositoryInfo/%s/json", 
							RepoUtils.split(server.url))).openConnection();
			connection.connect();
			int rc = connection.getResponseCode();
			if (rc == 200) {
				String line = null;
				BufferedReader br = new BufferedReader(
						new java.io.InputStreamReader(connection
								.getInputStream()));
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null)
					sb.append(line + '\n');

				JSONObject json = new JSONObject(sb.toString());
				JSONObject array = json.getJSONObject("listing");
				String avatar = array.getString("avatar");
				String name = array.getString("name");
				String downloads = array.getString("downloads");
				db.addStoreInfo(avatar,name,withSuffix(downloads),server.id);
			}
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			db.addStoreInfo("",RepoUtils.split(server.url),"0",server.id);
		}
		
		
	}
	public static String withSuffix(String input) {
		long count = Long.parseLong(input);
	    if (count < 1000) return "" + count;
	    int exp = (int) (Math.log(count) / Math.log(1000));
	    return String.format("%.1f %c",
	                         count / Math.pow(1000, exp),
	                         "kMGTPE".charAt(exp-1));
	}
}
