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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.webservices.comments.AddCommentDialog;
import cm.aptoide.pt.webservices.comments.Comment;
import cm.aptoide.pt.webservices.comments.CommentPosterListOnScrollListener;
import cm.aptoide.pt.webservices.comments.CommentsAdapter;
import cm.aptoide.pt.webservices.login.LoginDialog;
import cm.aptoide.pt.webservices.taste.AddTaste;
import cm.aptoide.pt.webservices.taste.EnumUserTaste;
import cm.aptoide.pt.webservices.taste.TastePoster;
import cm.aptoide.pt.webservices.taste.WrapperUserTaste;

public class ApkInfo extends FragmentActivity implements LoaderCallbacks<Cursor>,OnDismissListener{
	DownloadQueueService downloadQueueService;
	long id;
	long repo_id;
	DBHandler db;
	Context context;
	HashMap<String, String> elements;
	TextView name;
	CustomListView listView;
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
	private CommentsAdapter<Comment> commentAdapter;
	private WrapperUserTaste userTaste;
	private LinearLayout loadComLayout;
	private String repo;
	private String apkid;
	private String vername;
	private ImageView icon;
	ImageLoader loader;
	private TextView versionInfo;
	private CheckBox scheduledDownloadBox;
	private boolean isDefaultSelection;
	private boolean extended;
	RelativeLayout linearLayout;
	TextView textView;
	
	protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
	
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
		listView = (CustomListView) findViewById(R.id.listComments);
		type=getIntent().getStringExtra("type");
		LayoutInflater inflater = this.getLayoutInflater();
		linearLayout = (RelativeLayout)inflater.inflate(R.layout.headercomments,listView, false);
		this.likes = (TextView)linearLayout.findViewById(R.id.likes);
		this.dislikes = (TextView)linearLayout.findViewById(R.id.dislikes);
		this.like = ((ImageView)linearLayout.findViewById(R.id.likesImage));
		this.dislike = ((ImageView)linearLayout.findViewById(R.id.dislikesImage));
		this.userTaste = new WrapperUserTaste();
		version = (TextView) linearLayout.findViewById(R.id.versionInfo);
		rating = (RatingBar) linearLayout.findViewById(R.id.rating);
		apk_about = (TextView)linearLayout.findViewById(R.id.descript);
		listView.addHeaderView(linearLayout, null, false);
		textView = new TextView(this);
		textView.setText(this.getString(R.string.commentlabel));
		textView.setTextSize(20);
		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		textView.setPadding(0, 10, 0, 10);
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		listView.addHeaderView(textView);
		loadComLayout = (LinearLayout) inflater.inflate(R.layout.loadingfootercomments,listView, false);
		listView.addFooterView(loadComLayout);
		store = (TextView)linearLayout.findViewById(R.id.app_store);
		spinnerMulti = ((Spinner)linearLayout.findViewById(R.id.spinnerMultiVersion));
		action = (Button) findViewById(R.id.btinstall);
		description = (TextView) linearLayout.findViewById(R.id.descript);
		screenshots = (ViewPager) findViewById(R.id.screenShotsPager);
		commentAdapter = new CommentsAdapter<Comment>(this, R.layout.commentlistviewitem, new ArrayList<Comment>());
		versionInfo = (TextView) findViewById(R.id.versionInfo);
		scheduledDownloadBox = (CheckBox) findViewById(R.id.schedule_download_box);
		listView.setAdapter(commentAdapter);
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
				if(versions.size()==1){
					runOnUiThread(new Runnable() {
						
						public void run() {
							spinnerMulti.setVisibility(View.GONE);
						}
					});

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
			scheduledDownloadBox.setEnabled(false);
		}
		if(Integer.parseInt(elements.get("vercode"))<Integer.parseInt(elements.get("installedVercode"))){
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
		if(extended){
			loadScreenshots();
			loadCommentsAndTaste();
			findViewById(R.id.likesLayout).setVisibility(View.VISIBLE);
//			findViewById(R.id.screenshots_label).setVisibility(View.VISIBLE);
//			findViewById(R.id.screenShotsPager).setVisibility(View.VISIBLE);
			findViewById(R.id.indicator).setVisibility(View.VISIBLE);
			textView.setVisibility(View.VISIBLE);
			loadComLayout.setVisibility(View.VISIBLE);
//			listView.addFooterView(loadComLayout);
//			listView.addHeaderView(textView);
		}else{
			findViewById(R.id.likesLayout).setVisibility(View.GONE);
			findViewById(R.id.screenshots_label).setVisibility(View.GONE);
			findViewById(R.id.screenShotsPager).setVisibility(View.GONE);
			findViewById(R.id.indicator).setVisibility(View.GONE);
			textView.setVisibility(View.GONE);
			loadComLayout.setVisibility(View.GONE);
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
		linearLayout.findViewById(R.id.screenshots_label).setVisibility(View.GONE);
		linearLayout.findViewById(R.id.screenshots_container).setVisibility(View.GONE);
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
								linearLayout.findViewById(R.id.screenshots_container).setVisibility(View.VISIBLE);
								linearLayout.findViewById(R.id.screenshots_label).setVisibility(View.VISIBLE);
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
	
	private TastePoster tastePoster = null;
	private CommentPosterListOnScrollListener loadOnScrollCommentList;
	LoginDialog loginComments;
	final Runnable newVersionFetchComments = new Runnable(){


		public void run() {
			loadOnScrollCommentList.fetchNewApp(repo, apkid, vername);
		}
	};
	
	private void loadCommentsAndTaste() {
		try{
			loadOnScrollCommentList = new CommentPosterListOnScrollListener(this, commentAdapter, repo, apkid, vername, loadComLayout,repo_id);
			listView.setOnScrollListener(loadOnScrollCommentList);
			new Thread(newVersionFetchComments).start();
			

			listView.setOnItemClickListener(new OnItemClickListener(){
				

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(position==1){ // If comment app... option selected
//						Dialog commentDialog = new AddCommentDialog(ApkInfo.this, loadOnScrollCommentList, null, like, dislike, 
//								repo,
//				 				apkid, 
//				 				vername,
//				 				userTaste);
//						commentDialog.show();
						Intent i = new Intent(ApkInfo.this,AddComment.class);
						i.putExtra("repo", repo);
						i.putExtra("apkid", apkid);
						i.putExtra("vername", vername);
						startActivityForResult(i, 50);
					}
				}
			});
			
			
			
			
			this.like.setOnTouchListener(new OnTouchListener(){
			      public boolean onTouch(View view, MotionEvent e) {
			          switch(e.getAction())
			          {
			             case MotionEvent.ACTION_DOWN:
			            	 
			            	 if(sPref.getString(Configs.LOGIN_USER_NAME, null)==null || sPref.getString(Configs.LOGIN_PASSWORD, null)==null){				
			            		loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, 
			            										dislike, repo , 
			            										apkid, vername, EnumUserTaste.LIKE, userTaste);
								loginComments.setOnDismissListener(ApkInfo.this);
								loginComments.show();
							 }else{
								
								 new AddTaste(
						 				ApkInfo.this, 
						 				repo,
						 				apkid, 
						 				vername, 
						 				sPref.getString(Configs.LOGIN_USER_NAME, null), 
						 				sPref.getString(Configs.LOGIN_PASSWORD, null), 
						 				EnumUserTaste.LIKE, likes, dislikes, like, dislike, userTaste).submit();
								
							 } 
			            	 break;
			          }
			          return false;  //means that the listener dosen't consume the event
			      }
			});
			this.dislike.setOnTouchListener(new OnTouchListener(){
			      public boolean onTouch(View view, MotionEvent e) {
			          switch(e.getAction())
			          {
			             case MotionEvent.ACTION_DOWN:
			            	 
			            	  if(sPref.getString(Configs.LOGIN_USER_NAME, null)==null || sPref.getString(Configs.LOGIN_PASSWORD, null)==null){				
			            		  	loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, dislike, 
			            		  									repo, apkid, vername, EnumUserTaste.DONTLIKE, userTaste);
			            		  	loginComments.setOnDismissListener(ApkInfo.this);
									loginComments.show();
			            	  }else{
			            		  
			            		  new AddTaste(
								 		ApkInfo.this, 
								 		repo,
								 		apkid, 
								 		vername, 
								 		sPref.getString(Configs.LOGIN_USER_NAME, null), 
								 		sPref.getString(Configs.LOGIN_PASSWORD, null), 
								 		EnumUserTaste.DONTLIKE, likes, dislikes, like, dislike, userTaste).submit();
								 
			            	  }
			                  break;
			          }
			          return false;  //means that the listener dosen't consume the event
			      }
			});
			
			selectTaste(repo , apkid, vername, likes, dislikes, like, dislike, userTaste);
			
			
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void selectTaste(String repo, String apkid, String version, 
			TextView likes, TextView dontlikes, ImageView like, 
			ImageView dislike, WrapperUserTaste userTaste){
		
		likes.setText(this.getString(R.string.loading_likes));
		dislikes.setText("");
		dislike.setImageResource(R.drawable.dontlike);
		like.setImageResource(R.drawable.like);

		if(tastePoster!=null)
			tastePoster.cancel(true);
		
		
		
		tastePoster = new TastePoster(this, apkid, version, repo, likes, dontlikes, 
				like, dislike, sPref.getString( Configs.LOGIN_USER_ID , null),
				userTaste);
		tastePoster.execute();

	}
	public void onDismiss(DialogInterface dialog) {
		if(sPref.getString(Configs.LOGIN_USER_NAME, null)!=null && sPref.getString(Configs.LOGIN_PASSWORD, null)!=null){
			new AddTaste(
	 				ApkInfo.this, 
	 				repo,
	 				apkid, 
	 				vername, 
	 				sPref.getString(Configs.LOGIN_USER_NAME, null), 
	 				sPref.getString(Configs.LOGIN_PASSWORD, null), 
	 				((LoginDialog)dialog).getUserTaste(), likes, dislikes, like, dislike, userTaste).submit();
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0==50&&arg1==RESULT_OK){
			loadCommentsAndTaste();
			
		}else if(arg0==60&&arg1==RESULT_OK){
			loginComments.dismiss();
			loadCommentsAndTaste();
		}
		
	}
	
}

