/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
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
import android.app.AlertDialog.Builder;
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
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.services.ServiceDownloadManager;
import cm.aptoide.pt.views.ViewApk;
import cm.aptoide.pt.views.ViewCache;
import cm.aptoide.pt.views.ViewDownloadManagement;
import cm.aptoide.pt.R;
import cm.aptoide.pt.services.AIDLServiceDownloadManager;

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

	private boolean isRunning = false;

	private AIDLServiceDownloadManager serviceDownloadManager = null;

	private boolean serviceManagerIsBound = false;

	private ServiceConnection serviceManagerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDownloadManager = AIDLServiceDownloadManager.Stub.asInterface(service);
			serviceManagerIsBound = true;

			Log.v("Aptoide-IntentReceiver", "Connected to ServiceDownloadManager");

			continueLoading();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceManagerIsBound = false;
			serviceDownloadManager = null;

			Log.v("Aptoide-IntentReceiver", "Disconnected from ServiceDownloadManager");
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
        SetAptoideTheme.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		if(getIntent().getData()!=null){

			if(!isRunning){
				isRunning = true;

				if(!serviceManagerIsBound){
		    		bindService(new Intent(this, ServiceDownloadManager.class), serviceManagerConnection, Context.BIND_AUTO_CREATE);
		    	}

			}

		}
	}

	private void continueLoading(){
		TMP_MYAPP_FILE = getCacheDir()+"/myapp.myapp";
		db=Database.getInstance();
		String uri = getIntent().getDataString();
		System.out.println(uri);
		if(uri.startsWith("aptoiderepo")){

			ArrayList<String> repo = new ArrayList<String>();
			repo.add(uri.substring(14));
			Intent i = new Intent(IntentReceiver.this,MainActivity.class);
			i.putExtra("newrepo", repo);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(i);
			i = new Intent("pt.caixamagica.aptoide.NEWREPO");
			i.putExtra("newrepo", repo);
			if(ApplicationAptoide.MULTIPLESTORES){
				sendBroadcast(i);
			}
			finish();

		}else if(uri.startsWith("aptoidexml")){
			String repo = uri.substring(13);
			parseXmlString(repo);
			Intent i = new Intent(IntentReceiver.this,MainActivity.class);
			i.putExtra("newrepo", repo);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(i);
			i = new Intent("pt.caixamagica.aptoide.NEWREPO");
			i.putExtra("newrepo", repo);
			if(ApplicationAptoide.MULTIPLESTORES){
				sendBroadcast(i);
			}
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
					View simpleMessageView = LayoutInflater.from(this).inflate(R.layout.dialog_simple_message, null);
					Builder dialogBuilder = new AlertDialog.Builder(this).setView(simpleMessageView);
					final AlertDialog alertDialog = dialogBuilder.create();
					alertDialog.setIcon(android.R.drawable.ic_menu_more);
					alertDialog.setTitle(ApplicationAptoide.MARKETNAME);
					alertDialog.setCancelable(true);
					((TextView) simpleMessageView.findViewById(R.id.dialog_message)).setText(getString(R.string.installapp_alrt) +app.get("name")+"?");

					alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							ViewApk apk = new ViewApk();
							apk.setApkid(app.get("apkid"));
							apk.setName(app.get("name"));
							apk.setVercode(0);
							apk.setVername("");
							apk.generateAppHashid();
							try {
								serviceDownloadManager.callStartDownload(new ViewDownloadManagement(app.get("path"),apk,new ViewCache(apk.hashCode(), app.get("md5sum"),app.get("apkid"),"")));
							} catch (RemoteException e) {
								e.printStackTrace();
							}
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

	private void startMarketIntent(String param) {
		System.out.println(param);
		long id = db.getApkId(param);
		Intent i;
		if(id>0){
			i = new Intent(this,ApkInfo.class);
			i.putExtra("_id", id);
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
			i = new Intent("pt.caixamagica.aptoide.NEWREPO");
			i.putExtra("newrepo", server);
			if(ApplicationAptoide.MULTIPLESTORES){
				sendBroadcast(i);
			}
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

	@Override
	protected void onDestroy() {
		unbindService(serviceManagerConnection);
		super.onDestroy();
	}

}
