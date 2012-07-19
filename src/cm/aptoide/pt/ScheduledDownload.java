package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeoutException;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			redraw();
		}
	};
	
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
	
	HashMap<String,Planet> planets = new HashMap<String, ScheduledDownload.Planet>(); 
	private Button installButton;	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		db = new DBHandler(context);
		db.open();
		IntentFilter filter = new IntentFilter();
		filter.addAction("pt.caixamagica.aptoide.REDRAW");
		registerReceiver(receiver, filter);
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
			public void bindView(View convertView, Context arg1, Cursor c) {
				
				
				// Planet to display
			      Planet planet = planets.get(c.getString(0)); 

			      // The child views in each row.
			      CheckBox checkBox ; 
			      TextView textView ;
			      ImageView imageView ; 
			      
			      // Create a new row view
			      if ( convertView.getTag() == null ) {
			        
			        // Find the child views.
			        textView = (TextView) convertView.findViewById( R.id.name );
			        checkBox = (CheckBox) convertView.findViewById( R.id.schDwnChkBox );
			        imageView = (ImageView) convertView.findViewById(R.id.appicon);
			        // Optimization: Tag the row with it's child views, so we don't have to 
			        // call findViewById() later when we reuse the row.
			        convertView.setTag( new PlanetViewHolder(textView,checkBox,imageView) );

			        // If CheckBox is toggled, update the planet it is tagged with.
			        checkBox.setOnClickListener( new View.OnClickListener() {
			          public void onClick(View v) {
			            CheckBox cb = (CheckBox) v ;
			            Planet planet = (Planet) cb.getTag();
			            planet.setChecked( cb.isChecked() );
			          }
			        });        
			      }
			      // Reuse existing row view
			      else {
			        // Because we use a ViewHolder, we avoid having to call findViewById().
			        PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
			        checkBox = viewHolder.checkBox ;
			        textView = viewHolder.textView ;
			        imageView = viewHolder.imageView ;
			      }

			      // Tag the CheckBox with the Planet it is displaying, so that we can
			      // access the planet in onClick() when the CheckBox is toggled.
			      checkBox.setTag( planet ); 
			      
			      // Display planet data
			      checkBox.setChecked( planet.isChecked() );
			      textView.setText( planet.getName() );  
				
//				((TextView) v.findViewById(R.id.isinst)).setText(c.getString(3));
//				((TextView) v.findViewById(R.id.name)).setText(c.getString(2));
				imageLoader.DisplayImage(-1, c.getString(5), imageView, arg1);
				
			}
		};
		
		 lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick( AdapterView<?> parent, View item, 
		                               int position, long id) {
		        Planet planet = (Planet) ((PlanetViewHolder)item.getTag()).checkBox.getTag();
		        planet.toggleChecked();
		        PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();
		        viewHolder.checkBox.setChecked( planet.isChecked() );
		      }
		    });
		
		lv.setAdapter(adapter);
		installButton = (Button) findViewById(R.id.sch_down);
		installButton.setText(getText(R.string.schDown_installselected));
		installButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(isAllChecked()){
					for(String planet : planets.keySet()){
						if (planets.get(planet).checked){
							DownloadNode downloadnode = doDownloadNode(planet);
							if(downloadnode!=null){
								downloadQueueService.startDownload(downloadnode);
							}
							else{
								// Toast.makeText(context, getString(R.string.schDown_downerror, new Object[]{db.getScheduledDwnServer(sch_list.get(i).apkid)}), Toast.LENGTH_LONG).show();
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
		if(getIntent().hasExtra("downloadAll")){
			downloadAll=true;
		}
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
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			planets.put(c.getString(0),new Planet(c.getString(2), true));
		}
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
	
	private DownloadNode doDownloadNode(String id) {
		DownloadNode downloadNode = null;
		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	
		HashMap<String, String> map = db.getScheduledDownload(id);
		try{
			String apk_id = map.get("apkid");
			String ver =  map.get("vername");
//			Vector<ApkNode> apk_lst = db.getAll("abc");
			
			String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
			tmp_serv = db.getPathHash(apk_id, ver);
			System.out.println(ver+apk_id);
			String localPath = new String(LOCAL_APK_PATH+apk_id+ver+".apk");
			String appName = apk_id;
			appName= map.get("name");;
			downloadNode = tmp_serv.firstElement();
			downloadNode.setPackageName(apk_id);
			downloadNode.setAppName(appName);
			downloadNode.setLocalPath(localPath);
			downloadNode.version=ver;
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
		if(planets.isEmpty()){
			return false;
		}
		for(String planet : planets.keySet()){
			if (planets.get(planet).checked){
				return true;
			}
		}
		return false;
	}
	
	private void downloadAll() {
		
		AlertDialog alrt = new AlertDialog.Builder(context).create();
		alrt.setMessage(getText(R.string.schDown_install));
		alrt.setButton(Dialog.BUTTON_POSITIVE,getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				for(String planet : planets.keySet()){
						DownloadNode downloadnode = doDownloadNode(planet);
						if(downloadnode!=null){
							downloadQueueService.startDownload(downloadnode);
						}
				}
				finish();
				return;
			} }); 
		alrt.setButton(Dialog.BUTTON_NEGATIVE,getText(R.string.btn_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
				return;
			}});
		alrt.show();
		
		
		
		
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE,1, 0, R.string.schDown_invertselection).setIcon(R.drawable.ic_menu_invert);
		menu.add(Menu.NONE,2, 0, R.string.schDown_removeselected).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
		
		
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AlertDialog alrt = new AlertDialog.Builder(this).create();
		switch (item.getItemId()) {
		case 2:
			if(isAllChecked()){
				alrt.setMessage(getText(R.string.schDown_sureremove));
				alrt.setButton(Dialog.BUTTON_POSITIVE,getText(R.string.btn_yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						for(String planet : planets.keySet()){
							if (planets.get(planet).checked){
								HashMap<String, String> map = db.getScheduledDownload(planet);
								db.deleteScheduledDownload(map.get("apkid"),map.get("vername"));
							}
						}
						redraw();
						return;
					} }); 
				alrt.setButton(Dialog.BUTTON_NEGATIVE,getText(R.string.btn_no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}});
				alrt.show();
			} else {
				Toast.makeText(this, R.string.schDown_nodownloadselect,
						Toast.LENGTH_LONG).show();
			}

			break;

		case 1:
			for(String planet : planets.keySet()){
				planets.get(planet).toggleChecked();
			}
			adapter.notifyDataSetInvalidated();
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
		unregisterReceiver(receiver);
		
	}
	
	private static class Planet {
	    private String name = "" ;
	    private boolean checked = false ;
	    public Planet() {}
	    public Planet( String name ) {
	      this.name = name ;
	    }
	    public Planet( String name, boolean checked ) {
	      this.name = name ;
	      this.checked = checked ;
	    }
	    public String getName() {
	      return name;
	    }
	    public void setName(String name) {
	      this.name = name;
	    }
	    public boolean isChecked() {
	      return checked;
	    }
	    public void setChecked(boolean checked) {
	      this.checked = checked;
	    }
	    public String toString() {
	      return name ; 
	    }
	    public void toggleChecked() {
	      checked = !checked ;
	    }
	  }
	  
	  /** Holds child views for one row. */
	  private static class PlanetViewHolder {
	    public CheckBox checkBox ;
	    public TextView textView ;
	    public ImageView imageView ;
	    public PlanetViewHolder( TextView textView, CheckBox checkBox, ImageView imageView ) {
	      this.checkBox = checkBox ;
	      this.textView = textView ;
	      this.imageView = imageView ;
	    }
	  }
	
}
