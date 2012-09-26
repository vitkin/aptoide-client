package cm.aptoide.pt2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import cm.aptoide.pt2.Server.State;
import cm.aptoide.pt2.util.Base64;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.SparseArray;
import android.widget.Toast;

public class MainService extends Service {
//	Database db;
	private static boolean isParsing = false;
	String defaultXmlPath = "sdcard/.aptoide/info.xml";
	String defaultTopXmlPath = "sdcard/.aptoide/top.xml";
	static SparseArray<Server> serversParsing = new SparseArray<Server>();
	@Override
	public IBinder onBind(Intent intent) {
		registerReceiver(receiver , new IntentFilter("complete"));
		return new LocalBinder();
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			serversParsing.remove((int)((Server)intent.getParcelableExtra("server")).id);
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
	
	public String get(Server server,String xmlpath) throws MalformedURLException, IOException{
		getApplicationContext().sendBroadcast(new Intent("connecting"));
		String hash = "";

		if (server.delta != null) {
			hash = "?hash=" + server.delta;
		}
		String url = server.url + "info.xml" + hash;
		System.out.println(url);
		

		File f = new File(xmlpath);
			InputStream in = getInputStream(new URL(url), server.username,
					server.password);
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
	
	public String getTop(Server server,String xmlpath) throws MalformedURLException, IOException{
		File f = new File(xmlpath);
			getApplicationContext().sendBroadcast(new Intent("connecting"));
			String url = server.url + "top.xml";
			System.out.println(url);
	    	InputStream in = getInputStream(new URL(url),server.username,server.password);
	    	
	    	
	    	int i = 0;
	    	while(f.exists()){
	    		f = new File(xmlpath+i++);
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
    
    public InputStream getInputStream(URL url,String username, String password) throws IOException {
		URLConnection connection = url.openConnection();
		if(username!=null && password!=null){
			String basicAuth = "Basic " + new String(Base64.encode((username+":"+password).getBytes(),Base64.NO_WRAP ));
			connection.setRequestProperty ("Authorization", basicAuth);
		}
		BufferedInputStream bis = new BufferedInputStream(
				connection.getInputStream(), 8 * 1024);
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		return bis;

	}

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
			parseServer(db, server);
		} catch (Exception e){
			
		}
	}

	public void parseServer(final Database db, final Server server) throws MalformedURLException, IOException {
		
		
		if(serversParsing.get((int) server.id)==null){
			server.state=State.QUEUED;
			db.updateStatus(server);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try{
						parseTop(db, server);
						parseInfoXml(db, server);
					}catch (Exception e){
						server.state=State.FAILED;
						db.updateStatus(server);
						getApplicationContext().sendBroadcast(new Intent("status"));
						e.printStackTrace();
					}
					
				}
			}).start();
			
			serversParsing.put((int)server.id, server);	
		}
	}
	
	public boolean deleteStore(Database db, long id){
		if(serversParsing.get((int)id)==null){
			db.deleteServer(id,true);
			return true;
		}
		return false;
	}
	
	public void parseTop(Database db, Server server) throws MalformedURLException, IOException{
		final String path2 = getTop(server,defaultTopXmlPath);
		RepoParser.getInstance(db).parse2(new File(path2), server, Category.TOP);
	}
	
	public void parseInfoXml(Database db, Server server) throws MalformedURLException, IOException{
		final String path = get(server,defaultXmlPath);
		RepoParser.getInstance(db).parse(new File(path),server);
	}

}
