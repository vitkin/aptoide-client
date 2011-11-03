package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeoutException;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class ScheduledDownload extends ListActivity {
	
	private DownloadQueueService downloadQueueService;
	
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        downloadQueueService = ((DownloadQueueService.DownloadQueueBinder)serviceBinder).getService();

	        Log.d("Aptoide-BaseManagement", "DownloadQueueService bound to a ScheduledDOwnload");
	    }
	    
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        downloadQueueService = null;
	        
	        Log.d("Aptoide-BaseManagement","DownloadQueueService unbound from a Tab");
	    }

		

	};	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	private MyCustomAdapter mAdapter;
	private DbHandler db = new DbHandler(this);
	private Vector<ApkNode> sch_list;
	private Vector <String> schDownToDelete = new Vector<String>();
	private DownloadNode downloadNode;
	
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(this.getListView());
        getApplicationContext().bindService(new Intent(this, DownloadQueueService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        sch_list=db.getScheduledListNames();
        mAdapter = new MyCustomAdapter();
        for(int i =0;i!=sch_list.size();i++){
        	mAdapter.addName(sch_list.get(i).name);
        	mAdapter.addIconpath(getString(R.string.icons_path)+sch_list.get(i).apkid);
        	mAdapter.addVersion(sch_list.get(i).ver);
        }
        setListAdapter(mAdapter);
        if(sch_list.size()==0){
			Toast.makeText(this, "No Scheduled Downloads", Toast.LENGTH_LONG).show();
        }
        
    }
    

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		menu.add(Menu.NONE,2, 2, "Download Selected");
		menu.add(Menu.NONE,3, 3, "Invert Selection");
		menu.add(Menu.NONE,4, 3, "Remove Selected");
		
		
		
		return super.onCreateOptionsMenu(menu);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AlertDialog alrt = new AlertDialog.Builder(this).create();
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 4:
			
			alrt.setMessage("Are you sure you want to remove?");
			alrt.setButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					for(int i=0; i < getListAdapter().getCount(); i++){
						LinearLayout itemLayout = (LinearLayout)getListView().getChildAt(i);
						CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.schDwnChkBox);
							if(cb.isChecked()){
								db.deleteScheduledDownload(sch_list.get(i).apkid);
								
							}
						}
					redraw();
					return;
				} }); 
			alrt.setButton2(getText(R.string.btn_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}});
			alrt.show();
			
			break;
			
		case 2:
			
			String programs = "";
			boolean existsDownloads=false;
			
			for(int i=0; i < this.getListAdapter().getCount(); i++){
				LinearLayout itemLayout = (LinearLayout)getListView().getChildAt(i);
				CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.schDwnChkBox);

				if (cb.isChecked()){
					downloadQueueService.startDownload(doDownloadNode(i));
					programs=programs.concat("\n"+getListAdapter().getItem(i).toString());
					
					schDownToDelete.add(sch_list.get(i).apkid);
					existsDownloads = true;
				}


			}
			if(existsDownloads){
				alrt.setMessage("Delete Scheduled Downloads? "+programs);
				alrt.setButton(getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						for (String apkid : schDownToDelete){
							db.deleteScheduledDownload(apkid);
						}
						redraw();
						return;
					} }); 
				alrt.setButton2(getText(R.string.btn_no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}});
				alrt.show();
			}else{
				Toast.makeText(this, "No Downloads Selected", Toast.LENGTH_LONG).show();
			}
			break;
		case 3:
			for(int i=0; i < this.getListAdapter().getCount(); i++){
				LinearLayout itemLayout = (LinearLayout)getListView().getChildAt(i);
				CheckBox cb = (CheckBox)itemLayout.findViewById(R.id.schDwnChkBox);
					cb.toggle();
				}
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}


	private void downloadAll() {
		// TODO Auto-generated method stub
		for (int i =0; i!=sch_list.size();i++){
			downloadQueueService.startDownload(doDownloadNode(i));
		}
		
	}

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		 
		super.onCreateContextMenu(menu, v, menuInfo);
		 
		menu.add(0, 4, 0, "Remove Scheduled Download");
	}
	
	


	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case 4:
			db.deleteScheduledDownload(sch_list.get(itemInfo.position).apkid);
			sch_list.remove(itemInfo.position);
			redraw();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
		
		
	}


	private void redraw() {
		// TODO Auto-generated method stub
		sch_list=db.getScheduledListNames();
		if(sch_list.size()==0){
			Toast.makeText(this, "No Scheduled Downloads", 10000).show();
        }
		mAdapter = null;
		mAdapter = new MyCustomAdapter();
        for(int i =0;i!=sch_list.size();i++){
        	mAdapter.addName(sch_list.get(i).name);
        	mAdapter.addIconpath(getString(R.string.icons_path)+sch_list.get(i).apkid);
        	mAdapter.addVersion(sch_list.get(i).ver);
        }
        setListAdapter(mAdapter);
		
		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		downloadQueueService.startDownload(doDownloadNode(position));
		super.onListItemClick(l, v, position, id);
	}


	private DownloadNode doDownloadNode(int position) {
		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	
		try{
			String packageName=sch_list.get(position).apkid;
			String ver = sch_list.get(position).ver;
			Vector<ApkNode> apk_lst = db.getAll("abc");
			String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
			tmp_serv = db.getPathHash(packageName, ver);
			String localPath = new String(LOCAL_APK_PATH+packageName+".apk");
			String appName = packageName;
			for(ApkNode node: apk_lst){
				if(node.apkid.equals(packageName)){
					appName = node.name;
					break;
				}
			}
			downloadNode = tmp_serv.firstElement();
			downloadNode.setPackageName(packageName);
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


	private class MyCustomAdapter extends BaseAdapter {
 
        private ArrayList<String> name = new ArrayList<String>();
        private ArrayList<String> iconpath = new ArrayList<String>();
        private ArrayList<String> version = new ArrayList<String>();
        
        private LayoutInflater mInflater;
 
        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addName(final String item) {
            name.add(item);
            notifyDataSetChanged();
        }
        
        public void addIconpath(final String item) {
            iconpath.add(item);
            notifyDataSetChanged();
        }
        
        public void addVersion(final String item) {
            version.add("Version: "+item);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return name.size();
        }
 
        public String getItem(int position) {
            return name.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
          
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.sch_download, null);
                holder = new ViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.iconpath = (ImageView)convertView.findViewById(R.id.appicon);
                holder.version=(TextView)convertView.findViewById(R.id.isinst);
                
                
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            
            holder.version.setText(version.get(position));
            
            
            new Uri.Builder().build();
            holder.name.setText(name.get(position));
            holder.iconpath.setImageURI(Uri.parse(iconpath.get(position)));
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView name;
        public ImageView iconpath;
        public TextView version;
    }

	

}


