package cm.aptoide.pt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

public class IntentReceiver extends Activity {
	private String TMP_MYAPP_FILE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/myapp";
	private String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private HashMap<String, String> app;
	private ArrayList<String> server;
	private ServiceConnection conn = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadQueueService = ((DownloadQueueService.DownloadQueueBinder) service).getService();
		}
	};
	
	DownloadQueueService downloadQueueService;
	DBHandler db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getData()!=null){
			db=new DBHandler(this);
			db.open();
			String uri = getIntent().getDataString();
			System.out.println(uri);
			if(uri.startsWith("aptoiderepo")){
				
				ArrayList<String> repo = new ArrayList<String>();
				repo.add(uri.substring(14));
				Intent i = new Intent(IntentReceiver.this,Aptoide.class);
				i.putExtra("newrepo", repo);
				startActivity(i);
				finish();
			}else if(uri.startsWith("aptoidexml")){
				String repo = uri.substring(13);
				parseXmlString(repo);
				Intent i = new Intent(IntentReceiver.this,Aptoide.class);
				i.putExtra("newrepo", repo);
				startActivity(i);
				finish();
			}else if(uri.startsWith("market")){
				
				
				String params = uri.split("&")[0];
				String param = params.split("=")[1];
				if (param.contains("pname:")) {
					param = param.substring(6);
				} else if (param.contains("pub:")) {
					param = param.substring(4);
				}
				startMarketIntent(param);
			}else if(uri.startsWith("https://market.android.com/details?id=")){
				String param = uri.split("=")[1];
				startMarketIntent(param);
			}else{
				try{
					bindService(new Intent(this,DownloadQueueService.class), conn , Context.BIND_AUTO_CREATE);
					downloadMyappFile(getIntent().getDataString());
					parseXmlMyapp(TMP_MYAPP_FILE);
					
					if(app!=null&&!app.isEmpty()){
						AlertDialog alertDialog = new AlertDialog.Builder(this).create();
						alertDialog.setMessage("APK");
						alertDialog.setButton(Dialog.BUTTON_POSITIVE, "yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								
									DownloadNode downloadNode = new DownloadNode(app.get("path"), app.get("md5sum"), Integer.parseInt(app.get("size"))/1000, SDCARD+"/.aptoide/"+app.get("apkid")+".apk", app.get("apkid"));
									downloadNode.setAppName(app.get("name"));
									downloadQueueService.setCurrentContext(IntentReceiver.this);
									downloadQueueService.startDownload(downloadNode);
									proceed();
								
								
							}
						});
						alertDialog.show();
					}else{
						proceed();
					}
			
				
				
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			}
			
			
		}
		
	}

	private void startMarketIntent(String param) {
		long id = db.getApkId(param);
		Intent i;
		if(id>0){
			i = new Intent(this,ApkInfo.class);
			i.putExtra("id", id);
			i.putExtra("type", "market");
		}else{
			i = new Intent(this,SearchManager.class);
			i.putExtra("search", param);
		}
		
		startActivity(i);
		finish();
	}

	private void proceed() {
		if(server!=null){
			Intent i = new Intent(IntentReceiver.this,Aptoide.class);
			i.putExtra("newrepo", server);
			startActivity(i);
			finish();
		}
	}
	
	
	private void downloadMyappFile(String myappUri) throws Exception{
		try{
			
			BufferedInputStream getit = new BufferedInputStream(new URL(myappUri).openStream(),1024);

			File file_teste = new File(TMP_MYAPP_FILE);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(TMP_MYAPP_FILE);
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
//			AlertDialog p = new AlertDialog.Builder(this).create();
//			p.setTitle(getText(R.string.top_error));
//			p.setMessage(getText(R.string.aptoide_error));
//			p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
//			      public void onClick(DialogInterface dialog, int which) {
//			          return;
//			        } });
//			p.show();
			e.printStackTrace();
		}
	}
	
	private void parseXmlMyapp(String file) throws Exception{
	    try {
	    	
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
	    	
			MyappHandler handler = new MyappHandler();
			
	    	sp.parse(new File(file),handler);
	    	server = handler.getServers();
	    	app = handler.getApp();
	    	
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } catch (SAXException e) {
	    	e.printStackTrace();
	    } catch (ParserConfigurationException e) {
	    	e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(downloadQueueService!=null){
			unbindService(conn);
		}
		
	}
	
	private void parseXmlString(String file){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    try {
	    	
	    	SAXParser sp = spf.newSAXParser();
	    	XMLReader xr = sp.getXMLReader();
	    	MyappHandler handler = new MyappHandler();
	    	xr.setContentHandler(handler);
	    	
	    	InputSource is = new InputSource();
	    	is.setCharacterStream(new StringReader(file));
	    	xr.parse(is);
	    	server = handler.getServers();
	    	app = handler.getApp();
	    	
	    	
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	
	    } catch (SAXException e) {
	    	e.printStackTrace();
	    } catch (ParserConfigurationException e) {
	    	e.printStackTrace();
		}
	}
	
}
