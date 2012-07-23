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



import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;


import android.app.Dialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.webservices.comments.AddComment;
import cm.aptoide.pt.webservices.comments.Comments;
import cm.aptoide.pt.webservices.comments.ViewComments;
import cm.aptoide.pt.webservices.login.Login;
import cm.aptoide.pt.webservices.taste.EnumUserTaste;
import cm.aptoide.pt.webservices.taste.Likes;

public class ApkInfo extends FragmentActivity implements LoaderCallbacks<Cursor>{
	DownloadQueueService downloadQueueService;
	long id;
	long repo_id;
	DBHandler db;
	Context context;
	HashMap<String, String> elements;
	TextView name;
//	CustomListView listView;
	TextView apk_about;
	TextView version;
	private String type;
	private RatingBar rating;
	private TextView store;
	ArrayList<VersionApk> versions;
	String[] thumbnailList;
	private Spinner spinnerMulti;
	private Button action;
	private String actionString;
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
	SharedPreferences sPref;
	private EnumUserTaste userTaste;
//	private LinearLayout loadComLayout;
	private String repo;
	private String apkid;
	private String vername;
	private ImageView icon;
	ImageLoader loader;
	private TextView versionInfo;
	private CheckBox scheduledDownloadBox;
	private boolean isDefaultSelection;
	private boolean extended;
	LinearLayout linearLayout;
//	Button textView;
	private LinearLayout likesLinearLayout;
	
	protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
	boolean collapsed = true;
	
	int scrollPosition = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		isDefaultSelection = true;

		bindService(new Intent(this,DownloadQueueService.class), conn , Service.BIND_AUTO_CREATE);
		sPref=getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		id = getIntent().getExtras().getLong("id");
		db = new DBHandler(context);
		setContentView(R.layout.apk_info);
		name = (TextView) findViewById(R.id.app_name);
		icon = (ImageView) findViewById(R.id.app_hashid);
		loader = new ImageLoader(context);
		type=getIntent().getStringExtra("type");
		LayoutInflater inflater = this.getLayoutInflater();
		linearLayout = (LinearLayout)findViewById(R.id.commentContainer);
		likesLinearLayout = (LinearLayout)findViewById(R.id.likesLayout);
		version = (TextView) findViewById(R.id.versionInfo);
		rating = (RatingBar) findViewById(R.id.rating);
		apk_about = (TextView)findViewById(R.id.descript);
//		listView.addHeaderView(linearLayout, null, false);
//		textView = (Button) LayoutInflater.from(context).inflate(R.layout.button, null);
//		textView.setText(R.string.commentlabel);
		
//		listView.addHeaderView(textView);
//		loadComLayout = (LinearLayout) inflater.inflate(R.layout.loadingfootercomments,listView, false);
//		listView.addFooterView(loadComLayout);
		store = (TextView)findViewById(R.id.app_store);
		spinnerMulti = ((Spinner)findViewById(R.id.spinnerMultiVersion));
		action = (Button) findViewById(R.id.btinstall);
		description = (TextView) findViewById(R.id.descript);
		screenshots = (ViewPager) findViewById(R.id.screenShotsPager);
		versionInfo = (TextView) findViewById(R.id.versionInfo);
		scheduledDownloadBox = (CheckBox) findViewById(R.id.schedule_download_box);
//		listView.setAdapter(commentAdapter);
		Bundle bundle = new Bundle();
		getSupportLoaderManager().initLoader(0x20,bundle, this);
		
	}
	public Loader<Cursor> onCreateLoader(int arg0, final Bundle bundle) {
		SimpleCursorLoader a = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				if(!type.equals("featured")){
					if(bundle.containsKey("oldApk")){
						System.out.println(bundle.getLong("oldApk"));
						
						return db.getOldApk(bundle.getLong("oldApk"));
					}else{
						return db.getApk(id);
					}
				}else{
					return db.getFeaturedApk(id);
				}
				
			
				
			
				
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
				if(!type.equals("featured")){
					elements.put(
							"iconpath",
							db.getIconspath(repo_id)
									+ cursor.getString(cursor
											.getColumnIndex(DBStructure.COLUMN_APK_ICON)));
				}else{
					elements.put(
							"iconpath",
							db.getFeaturedIconspath(repo_id)
									+ cursor.getString(cursor
											.getColumnIndex(DBStructure.COLUMN_APK_ICON)));
				}
				
				elements.put("rating", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_RATING)));
				elements.put("vername", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_VERNAME)));
				elements.put("vercode", cursor.getString(cursor
						.getColumnIndex(DBStructure.COLUMN_APK_VERCODE)));
				if(!type.equals("featured")){
					elements.put("repo", db.getRepoName(repo_id));
				}
				elements.put("installedVercode", db.getInstalledVercode(elements.get("apkid")));
				elements.put("installedVername", db.getInstalledVername(elements.get("apkid")));
				
				if(!type.equals("featured")){
					extended=db.getExtendedServer(repo_id)==1;
				}else{
					extended = true;
				}
				
				versions = new ArrayList<VersionApk>();
				if(!type.equals("featured")){
					versions = db.getOldApks(elements.get("apkid"));
				}
				
				VersionApk versionApkPassed = new VersionApk(elements.get("vername"),Integer.parseInt(elements.get("vercode")),elements.get("apkid"),Integer.parseInt(elements.get("size")), Integer.parseInt(elements.get("downloads")));
				versions.add(versionApkPassed);
				Collections.sort(versions, Collections.reverseOrder());
//				if(versions.size()==1){
//					runOnUiThread(new Runnable() {
//						
//						public void run() {
//							spinnerMulti.setVisibility(View.GONE);
//						}
//					});

//				}
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
		
		apkid=elements.get("apkid");
		vername=elements.get("vername");
		if(!type.equals("featured")){
			repo = db.getRepoName(repo_id).split("\\.")[0];
			repo = repo.split("http://")[1];
		}else{
			repo=db.getEditorsChoiceRepoName(repo_id);
		}
		
		loader.DisplayImage(-1, elements.get("iconpath"), icon, context);
		name.setText(elements.get("name"));
		version.setText(elements.get("vername"));
		versionInfo.setText("Size: "+elements.get("size")+"KB Downloads: "+elements.get("downloads"));
		try{
			rating.setRating(Float.parseFloat(elements.get("rating")));
		}catch (Exception e) {
			rating.setRating(0);
		}
		action.setOnClickListener(installListener);
		if(type.equals("featured")){
			scheduledDownloadBox.setVisibility(View.GONE);
		}
		try{
			if(Integer.parseInt(elements.get("installedVercode"))>0){
				((TextView) findViewById(R.id.inst_version)).setText("Installed Version: " + elements.get("installedVername"));
				((TextView) findViewById(R.id.inst_version)).setVisibility(View.VISIBLE);
			}
			
			if(Integer.parseInt(elements.get("vercode"))<
					Integer.parseInt(elements.get("installedVercode"))){
				actionString = "Downgrade";
			}else if (Integer.parseInt(elements.get("vercode"))==Integer.parseInt(elements.get("installedVercode"))){
				actionString = "Uninstall";
				action.setOnClickListener(uninstallListener);
				scheduledDownloadBox.setEnabled(false);
			}else if (Integer.parseInt(elements.get("vercode"))>Integer.parseInt(elements.get("installedVercode"))&&!elements.get("installedVercode").equals("-1")){
				actionString = "Upgrade";
			}else{
				actionString = "Install";
			}
		}catch (Exception e){
			actionString = "Install";
		}
		
		store.setText(elements.get("repo"));
		description.setText(description_text);
		if(isDefaultSelection){
			final MultiversionSpinnerAdapter<VersionApk> spinnerMultiAdapter 
			= new MultiversionSpinnerAdapter<VersionApk>(this, R.layout.textviewfocused, versions);
			spinnerMultiAdapter.setDropDownViewResource(R.layout.multiversionspinneritem);
			spinnerMulti.setAdapter(spinnerMultiAdapter);
		}
		
		
		
		action.setText(actionString);
		Button serch_mrkt = (Button)findViewById(R.id.btmarket);
		serch_mrkt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+apkid));
				try{
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					Toast.makeText(context, getText(R.string.error_no_market), Toast.LENGTH_LONG).show();
				}
			}
			
		});
		scheduledDownloadBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					action.setText("Schedule");
				}else{
					action.setText(actionString);
				}
				
			}
		});
		
		spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				if(isDefaultSelection){
					System.out.println("On Item Click");	
					isDefaultSelection=false;
				}else{
					Bundle bundle = new Bundle();
					if(arg2>0){
						bundle.putLong("oldApk", db.getOldApkId(apkid,((VersionApk)spinnerMulti.getSelectedItem()).getVersion()));
					}else{
						id = db.getApkId(apkid);
					}
					getSupportLoaderManager().restartLoader(0x20, bundle, ApkInfo.this);
				}
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		findViewById(R.id.add_comment).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ApkInfo.this,cm.aptoide.pt.webservices.comments.AddComment.class);
				i.putExtra("apkid", apkid);
				i.putExtra("version", elements.get("vername"));
				i.putExtra("repo", repo);
				i.putExtra("webservicespath", db.getWebservicespath(repo_id));
				startActivityForResult(i, cm.aptoide.pt.webservices.comments.AddComment.ADD_COMMENT_REQUESTCODE);
			}
		});
		
		findViewById(R.id.likesImage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postLike(EnumUserTaste.LIKE);
				
			}
		});
		
		findViewById(R.id.dislikesImage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postLike(EnumUserTaste.DONTLIKE);
				
			}
		});
		
		findViewById(R.id.more_comments).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ApkInfo.this,ViewComments.class);
				i.putExtra("repo", repo);
				i.putExtra("apkid", apkid);
				i.putExtra("vername", elements.get("vername"));
				i.putExtra("webservicespath", db.getWebservicespath(repo_id));
				startActivity(i);
				
			}
		});
		if(description.getLineCount()>10){
			description.setMaxLines(10);
			findViewById(R.id.show_all_description).setVisibility(View.VISIBLE);
			findViewById(R.id.description_container).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(collapsed){
						collapsed=false;
						scrollPosition = (int)((ScrollView)findViewById(R.id.scrollView1)).getScrollY();
						description.setMaxLines(Integer.MAX_VALUE);
						((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_up, 0);
					}else{
						collapsed=true;
						((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_down, 0);
						description.setMaxLines(10);
						((ScrollView)findViewById(R.id.scrollView1)).scrollTo(0, scrollPosition);
					}
				}
			});
		}
		
		
		
		if(extended){
			loadScreenshots();
			loadCommentsAndTaste();
			findViewById(R.id.likesLayout).setVisibility(View.VISIBLE);
//			findViewById(R.id.screenshots_label).setVisibility(View.VISIBLE);
			findViewById(R.id.screenShotsPager).setVisibility(View.VISIBLE);
			findViewById(R.id.indicator).setVisibility(View.VISIBLE);
//			loadComLayout.setVisibility(View.VISIBLE);
//			listView.addFooterView(loadComLayout);
			
		}else{
			findViewById(R.id.likesLayout).setVisibility(View.GONE);
			findViewById(R.id.screenshots_label).setVisibility(View.GONE);
			findViewById(R.id.screenShotsPager).setVisibility(View.GONE);
			findViewById(R.id.indicator).setVisibility(View.GONE);
//			loadComLayout.setVisibility(View.GONE);
//			listView.removeFooterView(loadComLayout);
//			listView.removeHeaderView(textView);
		}
		
		AdView adView = (AdView)this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
	}
	
	

	
	private void loadScreenshots() {
		final CirclePageIndicator pi = (CirclePageIndicator) findViewById(R.id.indicator);
		pi.setFillColor(Color.BLACK);
		pi.setSnap(true);
		findViewById(R.id.screenshots_label).setVisibility(View.GONE);
		findViewById(R.id.screenshots_container).setVisibility(View.GONE);
		new Thread(new Runnable() {
			
			private JSONArray imagesurl;
			String uri;
			ArrayList<String> originalList;
			public void run() {
				try{
					if(!type.equals("featured")){
						HttpClient client = new DefaultHttpClient();
						HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
						HttpResponse response=null;
						HttpGet request = new HttpGet();
						uri = db.getWebservicespath(repo_id)+"webservices/listApkScreens/"+repo+"/"+elements.get("apkid")+"/"+elements.get("vername")+"/json";
						request.setURI(new URI(uri));
						System.out.println(request.getURI());
						response = client.execute(request);
						System.out.println(request.getURI()+"");
						String temp = EntityUtils.toString(response.getEntity());

						JSONObject respJSON;
						respJSON = new JSONObject(temp);

						imagesurl = respJSON.getJSONArray("listing");
						thumbnailList = new String[imagesurl.length()];

						for ( int i = 0; i!= imagesurl.length();i++){
							thumbnailList[i]=screenshotToThumb(imagesurl.getString(i));
						}

						originalList = new ArrayList<String>();
						for(int i=0;i < imagesurl.length();i++){ 
							originalList.add(imagesurl.getString(i));
						}
					}else{
						originalList = db.getFeaturedScreenhots(id,repo_id);
						thumbnailList = new String[originalList.size()];
						for(int i = 0; i!=originalList.size();i++){
							thumbnailList[i]=screenshotToThumb(originalList.get(i));
						}
						
					}
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					runOnUiThread(new Runnable() {

						public void run() {
							if(thumbnailList!=null&&thumbnailList.length>0){
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,thumbnailList,originalList));
								
								pi.setViewPager(screenshots);
								pi.setRadius(7.5f);
								findViewById(R.id.screenshots_container).setVisibility(View.VISIBLE);
								findViewById(R.id.screenshots_label).setVisibility(View.VISIBLE);
								if(originalList.size()==1){
									findViewById(R.id.right).setVisibility(View.GONE);
								}
								pi.setOnPageChangeListener(new OnPageChangeListener() {
									
									public void onPageSelected(int position) {
										
										findViewById(R.id.left).setVisibility(View.VISIBLE);
										findViewById(R.id.right).setVisibility(View.VISIBLE);
										
										if(position==0){
											findViewById(R.id.left).setVisibility(View.GONE);
										}
										if (position==originalList.size()-1){
											findViewById(R.id.right).setVisibility(View.GONE);
										}
										
										
										
										System.out.println(position + " " +originalList.size());
									}
									
									public void onPageScrolled(int arg0, float arg1, int arg2) {
										
									}
									
									public void onPageScrollStateChanged(int arg0) {
										
									}
								});
							}
							
						}
					});
				}
			}
		}).start();
	}
	
	protected String screenshotToThumb(String string) {
		
		String[] splitedString = string.split("/");
		StringBuilder db = new StringBuilder();
		for(int i = 0; i !=splitedString.length-1;i++){
			db.append(splitedString[i]);
			db.append("/");
		}
		db.append("thumbs/mobile/");
		db.append(splitedString[splitedString.length-1]);
		
		return db.toString();
	}

	private OnClickListener installListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(!scheduledDownloadBox.isChecked()){
				queueDownload(elements.get("apkid"), ((VersionApk) spinnerMulti.getSelectedItem()).getVersion(), false);
			}else{
				db.insertScheduledDownload(id,apkid,elements.get("name"),elements.get("iconpath"),vername,repo_id);
			}
			finish();
		}
	};
	
	private OnClickListener uninstallListener = new OnClickListener() {

		public void onClick(View v) {
			Uri uri = Uri.fromParts("package", apkid, null);
			Intent intent = new Intent(Intent.ACTION_DELETE, uri);
			startActivity(intent); 
			finish();
		}
	};
	
	protected void queueDownload(String packageName, String ver, boolean isUpdate){


		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	

		try{
			
			String appName = packageName;
			if(!type.equals("featured")){
				tmp_serv = db.getPathHash(packageName, ver);
				appName = db.getApkName(packageName);
			}else{
				tmp_serv = db.getFeaturedPathHash(packageName, ver);
				appName = db.getFeaturedApkName(packageName);
				
			}

			String localPath = new String(LOCAL_APK_PATH+packageName+"."+ver+".apk");
			
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
			logins = db.getLogin(downloadNode.getRepo());
			downloadNode.setLogins(logins);
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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if(requestCode==50&&resultCode==RESULT_OK){
			loadCommentsAndTaste();
			
		}else if(requestCode==60&&resultCode==RESULT_OK){
			loadCommentsAndTaste();
		}
		
		switch (requestCode) {
		case Login.REQUESTCODE:
			switch (resultCode) {
			case RESULT_OK:
				System.out.println("On Activity Result");
				postLike(userTaste);
				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
			break;
		case AddComment.ADD_COMMENT_REQUESTCODE:
			loadCommentsAndTaste();
		default:
			break;
		}
		
	}
	private void loadCommentsAndTaste() {
		Comments comments = new Comments(this, db.getWebservicespath(repo_id));
		comments.getComments(repo, apkid, elements.get("vername"), linearLayout,false);
		likes = new Likes(this, db.getWebservicespath(repo_id));
		likes.getLikes(repo, apkid, elements.get("vername"), likesLinearLayout);
	}
	
	
	Likes likes;
	protected void postLike(EnumUserTaste like) {
		userTaste = like;
		if(Login.isLoggedIn(this)){
			try{
				likes.postLike(repo, apkid, elements.get("vername"), like);	
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			Intent i = new Intent(this,Login.class);
			startActivityForResult(i, Login.REQUESTCODE);
		}
	}
	
	
	
}

