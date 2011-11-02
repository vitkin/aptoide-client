package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeoutException;


import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
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
	DownloadNode downloadNode;
	
	 
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
        
    }
    

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE,1, 1, "Download All");
		
		
		return super.onCreateOptionsMenu(menu);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			downloadAll();
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
			Toast.makeText(this, itemInfo.position+ " " + itemInfo.id, 5000).show();
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


