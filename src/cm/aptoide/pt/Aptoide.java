package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class Aptoide extends FragmentActivity {
	private String XML_PATH;
	Context context;
	ArrayList<View> pages;
	DBHandler db;
	ViewPager vp;
	ProgressDialog pd;
	CursorAdapter availAdapter;
	CursorAdapter installedAdapter;
	CursorAdapter updatesAdapter;
	ListView installed_listView;
	ListView updates_listView;
	ListView available_listView;
	ArrayList<ServerNode> servers;
	private int parsingProgress = 0;
	ImageView search;
	
	private int NEWREPO_FLAG = 0;
	
	private final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private String LOCAL_PATH = SDCARD+"/.aptoide";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aptoide);

		context = this;
		XML_PATH = getCacheDir()+"/temp_info.xml";
		db = new DBHandler(context);
		db.open();

		TitlePageIndicator pi = (TitlePageIndicator) findViewById(R.id.indicator);
		vp = (ViewPager) findViewById(R.id.viewpager);
		search = (ImageView) findViewById(R.id.btsearch);
		pages = new ArrayList<View>();

		available_listView = new ListView(context);
		installed_listView = new ListView(context);
		updates_listView = new ListView(context);
		
//		availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		
		installedAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		updatesAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		
		

		pages.add(available_listView);
		pages.add(installed_listView);
		pages.add(updates_listView);

		vp.setAdapter(new ViewPagerAdapter(context, pages));
		pi.setViewPager(vp);

		pi.setTextColor(Color.WHITE);
		pi.setFooterIndicatorStyle(IndicatorStyle.Triangle);
		pi.setSelectedColor(Color.LTGRAY);
//		db.beginTransation();
		redrawAll();
//		db.endTransation();
		available_listView.setAdapter(availAdapter);
		installed_listView.setAdapter(installedAdapter);
		updates_listView.setAdapter(updatesAdapter);
		
		available_listView.setOnItemClickListener(availItemClick);
		updates_listView.setOnItemClickListener(updatesItemClick);
		search.setOnClickListener(searchClick);
		
		File local_path = new File(LOCAL_PATH);
		if(!local_path.exists())
			local_path.mkdir();
		IntentFilter filter = new IntentFilter();
		filter.addAction("pt.caixamagica.aptoide.REDRAW");
		registerReceiver(receiver, filter);
		
		
		
		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (vp.getCurrentItem() == 0) {
			menu.add(0, 1, 0, "Update");
		}
		menu.add(0, 0, 0, "Manage Stores");
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			startActivityForResult(new Intent(this,StoreManager.class), 0);
		}else{
			updateRepos();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void updateRepos() {
		
		
		
		pd = new ProgressDialog(context);
		pd.setMessage("Please wait...");
		pd.show();
		
		parsingProgress=0;
		
		new Thread() {
			

			public void run() {
				try {
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					servers = new ArrayList<ServerNode>();
					servers = db.getInUseServers();
					for(ServerNode server : servers){
						parsingProgress++;
						int parse = downloadList(server.uri, server.hash);
						
						if(parse==0){
							
							sp.parse(new File(XML_PATH),new RepoParser(context, handler, server.id,XML_PATH));;
							
						}else if(parse==1){
							System.out.println("Repo need no update");
							runOnUiThread(new Runnable() {
								
								public void run() {
									Toast.makeText(context, "Repo need no update", 1).show();
									
								}
							});
						} else{
							runOnUiThread(new Runnable() {

								public void run() {
									Toast.makeText(context, "Repo failed", 1).show();

								}
							});
						}
						
						
					}
					Intent i = new Intent(Aptoide.this,ExtrasService.class);
					i.putExtra("repos", servers);
					startService(i);
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							redrawAll();
							
						}
					});
					new File(XML_PATH).delete();
					
					
				}
			};
		}.start();
	}

	Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what > 0) {
				pd.dismiss();
				pd=new ProgressDialog(context);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMessage("Parsing: "+parsingProgress+" of "+servers.size());
				pd.setMax(100);
				pd.show();
			} else {
				pd.incrementProgressBy(1);
			}
		}
	};
	

	private InputStream OpenHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			throw new IOException("Error connecting");
		}
		return in;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		super.onActivityResult(requestCode, arg1, data);
		if(requestCode == NEWREPO_FLAG && data != null && data.hasExtra("update")){
			updateRepos();
		}else if(requestCode == NEWREPO_FLAG && data != null && data.hasExtra("redraw")){
			runOnUiThread(new Runnable() {
				
				public void run() {
					redrawAll();
				}
			});
		}
	}
	
	private void redrawAll() {
		availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,false);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().destroyLoader(0x02);
		getSupportLoaderManager().destroyLoader(0x03);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getCategories1();
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				redrawInstalled();
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
	}
	
	private void redrawAvailable() {
		availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,false);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getCategories1();
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
	}
	
	public void redrawCategory(final String category){
		availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,true);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getCategories2(category);
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				System.out.println("Current Category 1:" + currentCategory1);
				System.out.println("Current Category 2:" + currentCategory2);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
		
	}
	
	public void redrawInstalled(){
		getSupportLoaderManager().restartLoader(0x02,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {

					@Override
					public Cursor loadInBackground() {
						return db.getInstalled();
					}
				};
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				installedAdapter.changeCursor(arg1);
				redrawUpdates();
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				installedAdapter.changeCursor(null);
				
			}
		});
	}
	
	public void redrawApkByCategory(final String category){
		availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getApkByCategory(category);
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				System.out.println("Current Category 1:" + currentCategory1);
				System.out.println("Current Category 2:" + currentCategory2);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
	}
	
	public void redrawUpdates() {
		getSupportLoaderManager().restartLoader(0x03,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {

					@Override
					public Cursor loadInBackground() {
						return db.getUpdates();
					}
				};
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				updatesAdapter.changeCursor(arg1);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				updatesAdapter.changeCursor(null);
			}
		});
	}
	
	private OnItemClickListener availItemClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(parent.getAdapter().getClass().getName().contains("CategoryCursorAdapter")){
				
				
				if(currentCategory1.equals("none")){
					String category = ((Cursor) parent.getAdapter().getItem(position)).getString(0);
					redrawCategory(category);
					currentCategory1=category;
				}else if(!currentCategory1.equals("none")){
					String category = ((Cursor) parent.getAdapter().getItem(position)).getString(0);
					redrawApkByCategory(category);
					currentCategory2=category;
				}
				
			}else if(parent.getAdapter().getClass().getName().contains("AvailableCursorAdapter")){
				Intent i = new Intent(Aptoide.this,ApkInfo.class);
				i.putExtra("id", parent.getAdapter().getItemId(position));
				i.putExtra("type", "available");
				startActivity(i);
			}
			
			
		}
	};
	
	private OnItemClickListener updatesItemClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long arg3) {
			Intent i = new Intent(Aptoide.this,ApkInfo.class);
			i.putExtra("id", parent.getItemIdAtPosition(position));
			i.putExtra("type", "updates");
			startActivity(i);			
		}
	};
	
	private OnClickListener searchClick = new OnClickListener() {
		
		public void onClick(View v) {
			onSearchRequested();
		}
	};
	
	String currentCategory1="none";
	String currentCategory2="none";
	private String REMOTE_FILE = "info.xml";
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(vp.getCurrentItem()==0){
			System.out.println("Current Category 1:" + currentCategory1);
			System.out.println("Current Category 2:" + currentCategory2);
			if(!currentCategory1.equals("none")){

				if(currentCategory2.equals("none")){
					redrawAvailable();
					currentCategory1="none";
					return false;
				}else if(!currentCategory1.equals("none")){
					redrawCategory(currentCategory1);
					currentCategory2="none";
					return false;
				}

			}else{

				return super.onKeyDown(keyCode, event);
			}
		}
		
		return super.onKeyDown(keyCode, event);
		
	}
	
	private int downloadList(String srv, String delta_hash){
		String url = srv+REMOTE_FILE ;
		try {

			//String delta_hash = db.getServerDelta(srv);
			if(delta_hash!=null && delta_hash.length()>2)
				url = url.concat("?hash="+delta_hash);

			Log.d("Aptoide","A fazer fetch info de: " + url);

			FileOutputStream saveit = new FileOutputStream(XML_PATH);


			HttpResponse mHttpResponse = null; //NetworkApis.getHttpResponse(url, srv, mctx);

			if(mHttpResponse == null){
				for(int xx=0; xx<2; xx++){
					try{
						mHttpResponse = NetworkApis.getHttpResponse(url, srv, context);
						if(mHttpResponse != null)
							break;
						else
							Log.d("Aptoide","--------------------->Connection is null");
					}catch (Exception e) {
						e.printStackTrace();
						continue;}
				}
				if(mHttpResponse == null)
					return -1;
			}

			Log.d("Aptoide","Got status: "+ mHttpResponse.getStatusLine().getStatusCode());

			if(mHttpResponse.getStatusLine().getStatusCode() == 200){
				Log.d("Aptoide","Got status 200");


				// see last-modified...
				MessageDigest md5hash = MessageDigest.getInstance("MD5");
				Header lst_modif = mHttpResponse.getLastHeader("Last-Modified");
				

				if(lst_modif != null){
					Log.d("Aptoide","lst_modif not null!");
					String lst_modif_str = lst_modif.getValue();
					String hash_lst_modif = new BigInteger(1,md5hash.digest(lst_modif_str.getBytes())).toString(16);
					Log.d("Aptoide","date is: " + lst_modif_str);
					Log.d("Aptoide","hash date: " + hash_lst_modif);

					String db_lst_modify = db.getUpdateTime(srv);
					if(db_lst_modify == null){
						db.setUpdateTime(hash_lst_modif, srv);
					}else{
						if(!db_lst_modify.equalsIgnoreCase(hash_lst_modif)){
							db.setUpdateTime(hash_lst_modif, srv);
						}else{
							Log.d("Aptoide","No update needed!");
							return 1;
						}
					}
				}
				//@dsilveira #531 +20lines fix OutOfMemory crash			
				InputStream instream = null;

				if((mHttpResponse.getEntity().getContentEncoding() != null) && (mHttpResponse.getEntity().getContentEncoding().getValue().equalsIgnoreCase("gzip"))){

					Log.d("Aptoide","with gzip");
					instream = new GZIPInputStream(mHttpResponse.getEntity().getContent());

				}else{

					Log.d("Aptoide","No gzip");
					instream = mHttpResponse.getEntity().getContent();

				}

				int nRead;
				byte[] data = new byte[1024];

				while ((nRead = instream.read(data, 0, data.length)) != -1) {
					saveit.write(data, 0, nRead);
				}


			}else{
				return -1;
				//Does nothing
			}
			return 0;
		} catch (UnknownHostException e){
			e.printStackTrace();
			return -1;
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
			return -1;} 
		catch (IOException e) { 
			e.printStackTrace();
			return -1;}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return -1;}
		catch (Exception e) {
			e.printStackTrace();
			return -1;}
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("pt.caixamagica.aptoide.REDRAW")){
				redrawInstalled();
			}
			
		}
	};
	
}
