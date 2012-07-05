package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class FailedDownloader extends Activity {

	
	DBHandler db;
	Context context;

	private DownloadQueueService downloadQueueService;
	private ServiceConnection conn = new ServiceConnection() {

		

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadQueueService=((DownloadQueueService.DownloadQueueBinder) service).getService();
			for(int i = 0;i!=failedDownloads.size();i++){
				queueDownload(i);
			}
			db.deleteFailedDownloads();
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		db=new DBHandler(this);
		db.open();
		failedDownloads = db.getFailedDownloads();
		bindService(new Intent(this,DownloadQueueService.class), conn , Service.BIND_AUTO_CREATE);
	}
	ArrayList<HashMap<String, String>> failedDownloads;
	
protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
	
	protected void queueDownload(int i){



		try{
			DownloadNode downloadNode = new DownloadNode("", failedDownloads.get(i).get("remotePath"), failedDownloads.get(i).get("md5sum"), Integer.parseInt(failedDownloads.get(i).get("intSize")),true);
			downloadNode.setPackageName(failedDownloads.get(i).get("packageName"));
			downloadNode.setAppName(failedDownloads.get(i).get("appName"));
			downloadNode.setLocalPath(failedDownloads.get(i).get("localPath"));
//			tmp_serv = db.getPathHash(packageName, ver);
//
//			String localPath = new String(LOCAL_APK_PATH+packageName+"."+ver+".apk");
//			String appName = packageName;
//			appName = db.getApkName(packageName);
//			//if(tmp_serv.size() > 0){
//			DownloadNode downloadNode = tmp_serv.firstElement();
//			downloadNode.setPackageName(packageName);
//			downloadNode.setAppName(appName);
//			downloadNode.setLocalPath(localPath);
//			downloadNode.setUpdate(isUpdate);
//			String remotePath = downloadNode.getRemotePath();
//			//}
//
//			if(remotePath.length() == 0)
//				throw new TimeoutException();
//
//			String[] logins = null; 
////			logins = db.getLogin(downloadNode.getRepo());
//			//			downloadNode.getRemotePath()
////			downloadNode.setLogins(logins);
//			Log.d("Aptoide-BaseManagement","queueing download: "+packageName +" "+downloadNode.getSize());	
//			downloadQueueService.setCurrentContext(context);
			downloadQueueService.startDownload(downloadNode);

		} catch(Exception e){	
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}
}
