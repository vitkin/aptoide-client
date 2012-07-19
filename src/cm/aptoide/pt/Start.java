package cm.aptoide.pt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Start extends FragmentActivity {
	Context context;
	TextView loading_text;
	DBHandler db;
	private HashMap<String,String> updateParams = new HashMap<String, String>();
	private static final String LATEST_VERSION_CODE_URI = "http://aptoide.com/latest_version.xml";
	private static final String TMP_UPDATE_FILE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/aptoideUpdate.apk";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
        db = new DBHandler(context);
        db.open();
		setContentView(R.layout.start);
		loading_text = (TextView) findViewById(R.id.loading_text);
		ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningTaskInfo> running = activityManager.getRunningTasks(Integer.MAX_VALUE);
    	
    	for (RunningTaskInfo runningTask : running) {
			if(runningTask.baseActivity.getClassName().equals("cm.aptoide.pt.Aptoide")){	//RemoteInTab is the real Aptoide Activity
				proceed();
	            return;
			}
		}
		
		
		//Check installed
		new Thread(new Runnable() {

			private PackageInfo pkginfo;

			public void run() {
				PackageManager mPm = getPackageManager();
				List<PackageInfo> system_installed_list = mPm.getInstalledPackages(0);
				List<String> database_installed_list = db.getStartupInstalled();
				db.beginTransation();
				for (PackageInfo pkg : system_installed_list) {
					if (!database_installed_list.contains(pkg.packageName)) {
						try {
							
							Apk apk = new Apk();
							apk.apkid = pkg.packageName;
							apk.vercode = pkg.versionCode;
							apk.vername = pkg.versionName;
							apk.name = (String) pkg.applicationInfo.loadLabel(mPm);
							db.insertInstalled(apk);
						} catch (Exception e) {
							//TODO Error manager
							e.printStackTrace();
						}finally{
							
						}
					}
				}
				db.endTransation();
				
				try{
					runOnUiThread(new Runnable() {
						
						public void run() {
							loading_text.setText("Checking for Aptoide update...");
							
						}
					});
					pkginfo = mPm.getPackageInfo("cm.aptoide.pt", 0);
					new Thread() {
						public void run(){
							try{
								
								getUpdateParameters();
								proceedhandler.postDelayed(new Run(false), 4000);
								
							if( pkginfo.versionCode < Integer.parseInt(updateParams.get("versionCode"))){
								Log.d("Aptoide-VersionCode", "Using version "+pkginfo.versionCode+", suggest update!");
								if(!flag)
									proceedhandler.post(new Run(true));
							}else{
								if(!flag)
									proceedhandler.post(new Run(false));
							}
							}catch(Exception e){
								proceedhandler.post(new Run(false));
							}
						}
					}.start();
				}catch (Exception e) {
				}finally{
				}
			}
		}).start();
        
    }

	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent arg2) {
		super.onActivityResult(requestCode, arg1, arg2);
		if(requestCode==0){
			finish();
		}else{
			proceed();
		}
		
		
	}
	
	private Handler proceedhandler = new Handler();
	private boolean flag;
	class Run implements Runnable{
		boolean update=false;
		
		public Run(boolean update) {
			this.update=update;
		}
		
		
		
		public void run() {
//			dialog.dismiss();
			if (!flag) {
				flag=true;
				if (update) {
					requestUpdateSelf();
				} else {
					proceed();
					
				}
			}
			
			
		}



		

		

		
		
	}
	
	private void requestUpdateSelf() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    	alertBuilder.setCancelable(false)
    				.setPositiveButton(R.string.dialog_yes , new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						dialog.cancel();
    						setContentView(R.layout.auto_updating);
    						new DownloadSelfUpdate().execute();
    					}
    				})    	
    				.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						dialog.cancel();
    						proceed();
    					}
    				})
    				.setMessage(R.string.update_self_msg)
    				;
    	
    	AlertDialog alert = alertBuilder.create();
    	
    	alert.setTitle(R.string.update_self_title);
    	alert.setIcon(R.drawable.icon);
    	
    	alert.show();	}
	
	private void proceed() {
		startActivityForResult(new Intent(Start.this,Aptoide.class),0);
	}
	
	private void getUpdateParameters() {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			
			URLConnection url = new URL(LATEST_VERSION_CODE_URI).openConnection();
			url.setReadTimeout(3000);
			url.setConnectTimeout(3000);
			
			InputStream a = new BufferedInputStream(url.getInputStream());
	        Document dom = builder.parse(new InputSource(a));
	        dom.getDocumentElement().normalize();
	        
	        NodeList items = dom.getElementsByTagName("versionCode");
	        if(items.getLength()>0){
	        	Node item = items.item(0);
	        	Log.d("Aptoide-XmlElement Name", item.getNodeName());
	        	Log.d("Aptoide-XmlElement Value", item.getFirstChild().getNodeValue().trim());
	        	updateParams.put("versionCode", item.getFirstChild().getNodeValue().trim());
	        }
	        
	        items = dom.getElementsByTagName("uri");
	        if(items.getLength()>0){
	        	Node item = items.item(0);
	        	Log.d("Aptoide-XmlElement Name", item.getNodeName());
	        	Log.d("Aptoide-XmlElement Value", item.getFirstChild().getNodeValue().trim());
	        	updateParams.put("uri", item.getFirstChild().getNodeValue().trim());
	        }
	        
	        items = dom.getElementsByTagName("md5");
	        if(items.getLength()>0){
	        	Node item = items.item(0);
	        	Log.d("Aptoide-XmlElement Name", item.getNodeName());
	        	Log.d("Aptoide-XmlElement Value", item.getFirstChild().getNodeValue().trim());
	        	updateParams.put("md5", item.getFirstChild().getNodeValue().trim());
	        }
	        
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private class DownloadSelfUpdate extends AsyncTask<Void, Void, Void>{
		private final ProgressDialog dialog = new ProgressDialog(context);
	
		String latestVersionUri;
		String referenceMd5;
		
		
		void retrieveUpdateParameters(){
			try{
				latestVersionUri = updateParams.get("uri");
				referenceMd5 = updateParams.get("md5");
			}catch (Exception e) {
				e.printStackTrace();
				Log.d("Aptoide-Auto-Update", "Update connection failed!  Keeping current version.");
			}
		}
		
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Retrieving update...");
			this.dialog.show();
			super.onPreExecute();
			retrieveUpdateParameters();
		}

		@Override
		protected Void doInBackground(Void... paramArrayOfParams) {
			try{
				if(latestVersionUri==null){
					retrieveUpdateParameters();
				}
//				Message msg_al = new Message();
				// If file exists, removes it...
				 File f_chk = new File(TMP_UPDATE_FILE);
				 if(f_chk.exists()){
					 f_chk.delete();
				 }
				 f_chk = null;
				
				FileOutputStream saveit = new FileOutputStream(TMP_UPDATE_FILE);
				DefaultHttpClient mHttpClient = new DefaultHttpClient();
				HttpGet mHttpGet = new HttpGet(latestVersionUri);
	
				HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
				
				if(mHttpResponse == null){
					 Log.d("Aptoide","Problem in network... retry...");	
					 mHttpResponse = mHttpClient.execute(mHttpGet);
					 if(mHttpResponse == null){
						 Log.d("Aptoide","Major network exception... Exiting!");
						 /*msg_al.arg1= 1;
						 download_error_handler.sendMessage(msg_al);*/
						 throw new TimeoutException();
					 }
				 }
				
				if(mHttpResponse.getStatusLine().getStatusCode() == 401){
					throw new TimeoutException();
				}else{
					InputStream getit = mHttpResponse.getEntity().getContent();
					byte data[] = new byte[8096];
					int bytesRead;
					bytesRead = getit.read(data, 0, 8096);
					while(bytesRead != -1) {
	//							download_tick.sendEmptyMessage(readed);
						saveit.write(data,0,bytesRead);
						bytesRead = getit.read(data, 0, 8096);
					}
					Log.d("Aptoide","Download done!");
					saveit.flush();
					saveit.close();
					getit.close();
				}
			}catch (Exception e) { 
//						download_error_handler.sendMessage(msg_al);
				e.printStackTrace();
				Toast.makeText(context, context.getString(R.string.network_auto_update_error), Toast.LENGTH_LONG);
				Log.d("Aptoide-Auto-Update", "Update connection failed!  Keeping current version.");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			super.onPostExecute(result);
			
			if(!(referenceMd5==null)){
				try{
					File apk = new File(TMP_UPDATE_FILE);
					Md5Handler hash = new Md5Handler();
					if( referenceMd5.equalsIgnoreCase(hash.md5Calc(apk))){
		//				msg_al.arg1 = 1;
		//						download_handler.sendMessage(msg_al);
						
						doUpdateSelf();
				    	
					}else{
						Log.d("Aptoide",referenceMd5 + " VS " + hash.md5Calc(apk));
		//				msg_al.arg1 = 0;
		//						download_error_handler.sendMessage(msg_al);
						throw new Exception(referenceMd5 + " VS " + hash.md5Calc(apk));
					}
				}catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(context, context.getString(R.string.md5_auto_update_error), Toast.LENGTH_LONG);
					Log.d("Aptoide-Auto-Update", "Update package checksum failed!  Keeping current version.");
					if (this.dialog.isShowing()) {
						this.dialog.dismiss();
					}
					proceed();
					super.onPostExecute(result);
					
				}
			}
			
		}
		
	}
	
	private void doUpdateSelf(){
		Intent intent = new Intent();
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	intent.setDataAndType(Uri.parse("file://" + TMP_UPDATE_FILE), "application/vnd.android.package-archive");
    	
    	startActivityForResult(intent, 99);
	}
    
    
}