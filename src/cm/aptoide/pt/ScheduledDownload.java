package cm.aptoide.pt;

import java.util.Vector;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduledDownload extends FragmentActivity implements LoaderCallbacks<Cursor>{
	ListView lv;
	Context context;
	DBHandler db;
	private CursorAdapter adapter;
	private DownloadQueueService downloadQueueService;
	private boolean downloadAll = false;
	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        downloadQueueService = ((DownloadQueueService.DownloadQueueBinder)serviceBinder).getService();
	        if(downloadAll){
	        	downloadAll();
	        }
	        downloadQueueService.setCurrentContext(context);
	        Log.d("Aptoide-BaseManagement", "DownloadQueueService bound to a ScheduledDOwnload");
	    }
	    
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        downloadQueueService = null;
	        downloadAll = false;
	        Log.d("Aptoide-BaseManagement","DownloadQueueService unbound from a Tab");
	    }

		

	};
	private Button installButton;	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		db = new DBHandler(context);
		db.open();
		setContentView(R.layout.sch_downloadempty);
		lv = (ListView) findViewById(android.R.id.list);
		bindService(new Intent(this,DownloadQueueService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		adapter =new CursorAdapter(context,null,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			ImageLoader imageLoader = new ImageLoader(context);
			@Override
			public View newView(Context context, Cursor arg1, ViewGroup v) {
				return LayoutInflater.from(context).inflate(R.layout.sch_download, null);
			}
			
			@Override
			public void bindView(View v, Context arg1, Cursor c) {
				((TextView) v.findViewById(R.id.isinst)).setText(c.getString(3));
				((TextView) v.findViewById(R.id.name)).setText(c.getString(2));
				imageLoader.DisplayImage(-1, c.getString(5), (ImageView) v.findViewById(R.id.appicon), arg1);
				
			}
		};
		lv.setAdapter(adapter);
		installButton = (Button) findViewById(R.id.sch_down);
		 installButton.setText(getText(R.string.schDown_installselected));
	        installButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if(isAllChecked()){
						for(int i=0; i < adapter.getCount(); i++){
							LinearLayout itemLayout = (LinearLayout)lv.getChildAt(i);
							CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.schDwnChkBox);

							if (cb.isChecked()){
								DownloadNode downloadnode = doDownloadNode(i);
								if(downloadnode!=null){
									downloadQueueService.startDownload(downloadnode);
								}
								else{
//									Toast.makeText(context, getString(R.string.schDown_downerror, new Object[]{db.getScheduledDwnServer(sch_list.get(i).apkid)}), Toast.LENGTH_LONG).show();

								}
							}
						}

					} else {
						Toast.makeText(context, R.string.schDown_nodownloadselect,
								Toast.LENGTH_LONG).show();
					}
					
				}
			});
		redraw();
	}
	private void redraw() {
		getSupportLoaderManager().restartLoader(0x50, null, this);
	}
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader a = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				
				return db.getScheduledDownloads();
			}
		};
		return a;
	}
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		adapter.swapCursor(c);
		if(c.getCount()==0){
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}else{
			findViewById(android.R.id.empty).setVisibility(View.GONE);
		}
	}
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	private DownloadNode doDownloadNode(int position) {
		DownloadNode downloadNode = null;
		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	
		try{
			String apk_id=((Cursor) adapter.getItem(position)).getString(1);
			String ver = ((Cursor) adapter.getItem(position)).getString(3);
//			Vector<ApkNode> apk_lst = db.getAll("abc");
			
			String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
			tmp_serv = db.getPathHash(apk_id, ver);
			System.out.println(ver+apk_id);
			String localPath = new String(LOCAL_APK_PATH+apk_id+ver+".apk");
			String appName = apk_id;
			appName=((Cursor) adapter.getItem(position)).getString(3);
			downloadNode = tmp_serv.firstElement();
			downloadNode.setPackageName(apk_id);
			downloadNode.setAppName(appName);
			downloadNode.setLocalPath(localPath);
			downloadNode.setUpdate(false);
			String remotePath = downloadNode.getRemotePath();
			if(remotePath.length() == 0)
				throw new TimeoutException();
			String[] logins = null; 
			logins = db.getLogin(downloadNode.getRepo());
			downloadNode.setLogins(logins);
		} catch(Exception e){	
			e.printStackTrace();
		}
		return downloadNode;
	}
	
	
	private boolean isAllChecked(){
		if(this.adapter.isEmpty()){
			return false;
		}
		for(int i=0; i < adapter.getCount(); i++){
			LinearLayout itemLayout = (LinearLayout)lv.getChildAt(i);
			CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.schDwnChkBox);
				if(cb.isChecked())
					return true;
			}
		return false;
	}
	
	private void downloadAll() {
		
		AlertDialog alrt = new AlertDialog.Builder(context).create();
		alrt.setMessage(getText(R.string.schDown_install));
		alrt.setButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				for (int i =0; i!=adapter.getCount();i++){
					DownloadNode downloadnode = doDownloadNode(i);
					if(downloadnode!=null){
						downloadQueueService.startDownload(downloadnode);
					}
					else{
//						Toast.makeText(getApplicationContext(), getString(R.string.schDown_downerror, new Object[]{db.getScheduledDwnServer(sch_list.get(i).apkid)}), Toast.LENGTH_LONG).show();
					}
				}
				finish();
				return;
			} }); 
		alrt.setButton2(getText(R.string.btn_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
				return;
			}});
		alrt.show();
		
		
		
		
		
		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
}
