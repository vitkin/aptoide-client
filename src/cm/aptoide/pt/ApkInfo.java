package cm.aptoide.pt;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ApkInfo extends FragmentActivity implements LoaderCallbacks<Cursor>{
	DownloadQueueService downloadQueueService;
	long id;
	long repo_id;
	DBHandler db;
	Context context;
	HashMap<String, String> elements;
	TextView name;
	ListView listView;
	private TextView likes;
	private TextView dislikes;
	private ImageView like;
	private ImageView dislike;
	TextView apk_about;
	TextView version;
	private String type;
	private RatingBar rating;
	private TextView store;
	ArrayList<VersionApk> versions;
	String[] images;
	private Spinner spinnerMulti;
	private Button action;
	private ServiceConnection conn = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadQueueService=((DownloadQueueService.DownloadQueueBinder) service).getService();
		}
	};
	private TextView description;
	ViewPager screenshots;
	private String description_text;
	
	
	protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		bindService(new Intent(this,DownloadQueueService.class), conn , Service.BIND_AUTO_CREATE);
		id = getIntent().getExtras().getLong("id");
		type = getIntent().getExtras().getString("type");
		db = new DBHandler(context);
		setContentView(R.layout.apk_info);
		name = (TextView) findViewById(R.id.app_name);
		
		listView = (ListView) findViewById(R.id.listComments);
		
		LayoutInflater inflater = this.getLayoutInflater();
		final RelativeLayout linearLayout = (RelativeLayout)inflater.inflate(R.layout.headercomments,listView, false);
		
		this.likes = (TextView)linearLayout.findViewById(R.id.likes);
		this.dislikes = (TextView)linearLayout.findViewById(R.id.dislikes);
		
		this.like = ((ImageView)linearLayout.findViewById(R.id.likesImage));
		this.dislike = ((ImageView)linearLayout.findViewById(R.id.dislikesImage));
		version = (TextView) linearLayout.findViewById(R.id.versionInfo);
		rating = (RatingBar) linearLayout.findViewById(R.id.rating);
		apk_about = (TextView)linearLayout.findViewById(R.id.descript);
		listView.addHeaderView(linearLayout, null, false);
		store = (TextView)linearLayout.findViewById(R.id.app_store);
		spinnerMulti = ((Spinner)linearLayout.findViewById(R.id.spinnerMultiVersion));
		action = (Button) findViewById(R.id.btinstall);
		description = (TextView) linearLayout.findViewById(R.id.descript);
		screenshots = (ViewPager) findViewById(R.id.screenShotsPager);
		
		
//		commentAdapter = new CommentsAdapter<Comment>(this, R.layout.commentlistviewitem, new ArrayList<Comment>());
		listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,new String[]{}));
		
		getSupportLoaderManager().initLoader(0x20, null, this);
		
	}
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader a = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				return db.getApk(id);
			}
		};
		return a;
	}
	public void onLoadFinished(Loader<Cursor> arg0, final Cursor cursor) {
		elements = new HashMap<String, String>();
		new Thread(new Runnable() {
			

			public void run() {
				try{
				repo_id = cursor.getLong(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_REPO_ID));
				elements.put("name", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_NAME)));
				elements.put("apkid", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_APKID)));
				elements.put("path", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_PATH)));
				elements.put("size", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_SIZE)));
				elements.put("downloads", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_DOWNLOADS)));
				elements.put(
						"iconpath",
						db.getIconspath(repo_id)
								+ cursor.getString(cursor
										.getColumnIndex(DBStructure.COLUMN_APK_ICON)));
				elements.put("rating", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_RATING)));
				elements.put("vername", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_VERNAME)));
				elements.put("vercode", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_VERCODE)));
				elements.put("repo", db.getRepoName(repo_id));
				
				
				versions = db.getOldApks(elements.get("apkid"));
				VersionApk versionApkPassed = new VersionApk(elements.get("vername"),Integer.parseInt(elements.get("vercode")),elements.get("apkid"),Integer.parseInt(elements.get("size")), Integer.parseInt(elements.get("downloads")));
				versions.add(versionApkPassed);
				Collections.sort(versions, Collections.reverseOrder());
				if(versions.size()==1){
					spinnerMulti.setVisibility(View.GONE);
				}
				Cursor c = getContentResolver().query(ExtrasContentProvider.CONTENT_URI, new String[]{ExtrasDBStructure.COLUMN_COMMENTS_COMMENT}, ExtrasDBStructure.COLUMN_COMMENTS_APKID+"=?", new String[]{elements.get("apkid")}, null);
				c.moveToFirst();
				if(c.getCount()>0){
					description_text = c.getString(0);
				}else{
					description_text="No description available.";
				}
				
				c.close();
				
				
				
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					runOnUiThread(new Runnable() {
						
						public void run() {
							loadElements();
							
						}
					});
				}
			}
		}).start();
		
	
	}
	
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	private void loadElements() {
		name.setText(elements.get("name"));
		version.setText(elements.get("vername"));
		try{
			rating.setRating(Float.parseFloat(elements.get("rating")));
		}catch (Exception e) {
			rating.setRating(0);
		}
		
		store.setText(elements.get("repo"));
		description.setText(description_text);
		final MultiversionSpinnerAdapter<VersionApk> spinnerMultiAdapter 
		= new MultiversionSpinnerAdapter<VersionApk>(this, R.layout.textviewfocused, versions);
		spinnerMultiAdapter.setDropDownViewResource(R.layout.multiversionspinneritem);
		spinnerMulti.setAdapter(spinnerMultiAdapter );
		action.setOnClickListener(installListener);
		screenshots.setVisibility(View.GONE);
		new Thread(new Runnable() {
			
			private JSONArray imagesurl;

			public void run() {
				try{
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
					HttpResponse response=null;
					HttpGet request = new HttpGet();
					String repo = db.getRepoName(repo_id).split(".bazaarandroid.com/")[0];
					repo = repo.split("http://")[1];
					request.setURI(new URI(db.getWebservicespath(repo_id)+"webservices/listApkScreens/"+repo+"/"+elements.get("apkid")+"/"+elements.get("vername")+"/json"));
					System.out.println(request.getURI());
					response = client.execute(request);
					System.out.println(request.getURI()+"");
					String temp = EntityUtils.toString(response.getEntity());

					JSONObject respJSON;
					respJSON = new JSONObject(temp);

					imagesurl = respJSON.getJSONArray("listing");
					images = new String[imagesurl.length()];
					for ( int i = 0; i!= imagesurl.length();i++){
						images[i]=imagesurl.getString(i);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					runOnUiThread(new Runnable() {

						public void run() {
							if(images!=null&&images.length>0){
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,images));
								screenshots.setVisibility(View.VISIBLE);
							}
							
						}
					});
				}
			}
		}).start();
	}
	
	private OnClickListener installListener = new OnClickListener() {
		
		public void onClick(View v) {
			queueDownload(elements.get("apkid"), ((VersionApk) spinnerMulti.getSelectedItem()).getVersion(), false);
		}
	};
	
	protected void queueDownload(String packageName, String ver, boolean isUpdate){


		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	

		try{

			tmp_serv = db.getPathHash(packageName, ver);

			String localPath = new String(LOCAL_APK_PATH+packageName+".apk");
			String appName = packageName;

			//if(tmp_serv.size() > 0){
			DownloadNode downloadNode = tmp_serv.firstElement();
			downloadNode.setPackageName(packageName);
			downloadNode.setAppName(appName);
			downloadNode.setLocalPath(localPath);
			downloadNode.setUpdate(isUpdate);
			String remotePath = downloadNode.getRemotePath();
			//}

			if(remotePath.length() == 0)
				throw new TimeoutException();

			String[] logins = null; 
//			logins = db.getLogin(downloadNode.getRepo());
			//			downloadNode.getRemotePath()
//			downloadNode.setLogins(logins);
			Log.d("Aptoide-BaseManagement","queueing download: "+packageName +" "+downloadNode.getSize());	
			downloadQueueService.setCurrentContext(context);
			downloadQueueService.startDownload(downloadNode);

		} catch(Exception e){	}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
	}
	
}

