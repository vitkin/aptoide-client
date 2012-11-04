package cm.aptoide.pt2;

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

import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownloadManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

public class IntentReceiver extends Activity implements OnDismissListener{
//	private String TMP_MYAPP_FILE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/myapp";
	String TMP_MYAPP_FILE;
	private String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private HashMap<String, String> app;
	private ArrayList<String> server;
	Database db;
	private OnClickListener neutralListener =  new OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			return;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getData()!=null){
			TMP_MYAPP_FILE = getCacheDir()+"/myapp.myapp";
			db=Database.getInstance(this);
			String uri = getIntent().getDataString();
			System.out.println(uri);
			if(uri.startsWith("aptoiderepo")){
				
				ArrayList<String> repo = new ArrayList<String>();
				repo.add(uri.substring(14));
				Intent i = new Intent(IntentReceiver.this,MainActivity.class);
				i.putExtra("newrepo", repo);
				i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(i);
				finish();
			}else if(uri.startsWith("aptoidexml")){
				String repo = uri.substring(13);
				parseXmlString(repo);
				Intent i = new Intent(IntentReceiver.this,MainActivity.class);
				i.putExtra("newrepo", repo);
				i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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
			}else if(uri.startsWith("http://market.android.com/details?id=")){
				String param = uri.split("=")[1];
				startMarketIntent(param);
			}else{
				try{
					System.out.println(getIntent().getDataString());
					downloadMyappFile(getIntent().getDataString());
					parseXmlMyapp(TMP_MYAPP_FILE);
					
					if(app!=null&&!app.isEmpty()){
						AlertDialog alertDialog = new AlertDialog.Builder(this).create();
						alertDialog.setMessage(getString(R.string.installapp_alrt) +app.get("name")+"?");
						alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								ViewApk apk = new ViewApk();
								apk.setApkid(app.get("apkid"));
								apk.setVercode(0);
								apk.generateAppHashid();
								new ViewDownloadManagement((ApplicationServiceManager) getApplication(),app.get("path"),apk,new ViewCache(apk.hashCode(), app.get("md5sum"))).startDownload();
							}
						});
						alertDialog.setButton(Dialog.BUTTON_NEGATIVE,getString(android.R.string.no), neutralListener );
						alertDialog.setOnDismissListener(this);
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
		System.out.println(param);
		long id = db.getApkId(param);
		Intent i;
		if(id>0){
			i = new Intent(this,ApkInfo.class);
			i.putExtra("id", id);
			i.putExtra("top", false);
			i.putExtra("category", Category.INFOXML.ordinal());
		}else{
			i = new Intent(this,SearchManager.class);
			i.putExtra("search", param);
		}
		
		startActivity(i);
		finish();
	}

	private void proceed() {
		if(server!=null){
			Intent i = new Intent(IntentReceiver.this,MainActivity.class);
			i.putExtra("newrepo", server);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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

	public void onDismiss(DialogInterface dialog) {
		proceed();
	}
	
}
