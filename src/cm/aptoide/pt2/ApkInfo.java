package cm.aptoide.pt2;

import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt2.adapters.ViewPagerAdapterScreenshots;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt2.services.AIDLServiceDownloadManager;
import cm.aptoide.pt2.services.ServiceDownloadManager;
import cm.aptoide.pt2.util.NetworkUtils;
import cm.aptoide.pt2.util.RepoUtils;
import cm.aptoide.pt2.util.quickaction.ActionItem;
import cm.aptoide.pt2.util.quickaction.EnumQuickActions;
import cm.aptoide.pt2.util.quickaction.QuickAction;
import cm.aptoide.pt2.views.EnumDownloadFailReason;
import cm.aptoide.pt2.views.EnumDownloadStatus;
import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownload;
import cm.aptoide.pt2.views.ViewDownloadManagement;
import cm.aptoide.pt2.webservices.comments.AddComment;
import cm.aptoide.pt2.webservices.comments.Comments;
import cm.aptoide.pt2.webservices.comments.ViewComments;
import cm.aptoide.pt2.webservices.login.Login;
import cm.aptoide.pt2.webservices.taste.EnumUserTaste;
import cm.aptoide.pt2.webservices.taste.Likes;

import com.viewpagerindicator.CirclePageIndicator;

public class ApkInfo extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	private ViewApk viewApk = null;
	private Database db;
	private Spinner spinner;
	SimpleCursorAdapter adapter;
	long id;
	Category category;
	Activity context;
	boolean spinnerInstanciated = false;
	CheckBox scheduledDownloadChBox;
	private ViewDownloadManagement download;

	
	private boolean isRunning = false;

	private AIDLServiceDownloadManager serviceDownloadManager = null;

	private boolean serviceManagerIsBound = false;

	private ServiceConnection serviceManagerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDownloadManager = AIDLServiceDownloadManager.Stub.asInterface(service);
			serviceManagerIsBound = true;
			
			Log.v("Aptoide-ApkInfo", "Connected to ServiceDownloadManager");
	        
			continueLoading();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceManagerIsBound = false;
			serviceDownloadManager = null;
			
			Log.v("Aptoide-ApkInfo", "Disconnected from ServiceDownloadManager");
		}
	};
	
	private AIDLDownloadObserver.Stub serviceDownloadManagerCallback = new AIDLDownloadObserver.Stub() {
		@Override
		public void updateDownloadStatus(ViewDownload update) throws RemoteException {
			download.updateProgress(update);
			handler.sendEmptyMessage(update.getStatus().ordinal());
		}
	};

	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.app_info);
		
		if(!isRunning){
			isRunning = true;

			if(!serviceManagerIsBound){
	    		bindService(new Intent(this, ServiceDownloadManager.class), serviceManagerConnection, Context.BIND_AUTO_CREATE);
	    	}
			
		}
		
	}
	
	private void continueLoading(){
		category = Category.values()[getIntent().getIntExtra("category", 3)];
		context = this;
		db = Database.getInstance(this);
		id = getIntent().getExtras().getLong("_id");
		scheduledDownloadChBox = (CheckBox) findViewById(R.id.schedule_download_box);
		loadElements(id);
		
		if(category.equals(Category.INFOXML)){
			spinner = (Spinner) findViewById(R.id.spinnerMultiVersion);
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_spinner_item, null,
					new String[] { "vername" ,"repo_id"}, new int[] { android.R.id.text1 },
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
					((TextView) arg0).setText(getString(R.string.version)+" " + arg1.getString(arg2) +" - "+RepoUtils.split(db.getServer(arg1.getLong(3),false).url));
					System.out.println("repo_id="+arg1.getString(3));
					return true;
				}
			});
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (spinnerInstanciated) {
						if(!download.isNull()){
							try {
								serviceDownloadManager.callUnregisterDownloadObserver(viewApk.hashCode());
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						loadElements(arg3);
					} else {
						spinnerInstanciated = true;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			getSupportLoaderManager().initLoader(0, null, this);


		}
	}
	
	
	String[] thumbnailList = null;
	String webservicespath= null;
	Likes likes;
	private EnumUserTaste userTaste;
	
	private void loadElements(long id) {
		findViewById(R.id.downloading_icon).setVisibility(View.GONE);
		findViewById(R.id.downloading_name).setVisibility(View.GONE);
		findViewById(R.id.download_progress).setVisibility(View.GONE);
		ProgressBar progress = (ProgressBar) findViewById(R.id.downloading_progress);
		progress.setIndeterminate(true);
		System.out.println("loading " + id + " " +category.name());
		if(category.equals(Category.ITEMBASED)){
			viewApk = db.getItemBasedApk(id);
		}else{
			viewApk = db.getApk(id, getIntent().getExtras().getBoolean("top", false));
		}
		
		
		try {
			download = serviceDownloadManager.callGetAppDownloading(viewApk.hashCode());
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		if(!download.isNull()){
			Button manage = (Button) findViewById(R.id.icon_manage);
			manage.setVisibility(View.GONE);
			manage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setupQuickActions(false, view);
				}
			});
			try {
				serviceDownloadManager.callRegisterDownloadObserver(viewApk.hashCode(), serviceDownloadManagerCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			findViewById(R.id.download_progress).setVisibility(View.VISIBLE);
			findViewById(R.id.icon_manage).setVisibility(View.VISIBLE);
			findViewById(R.id.downloading_name).setVisibility(View.INVISIBLE);
			((ProgressBar) findViewById(R.id.downloading_progress)).setProgress(download.getProgress());
			((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString(this));
			((TextView) findViewById(R.id.speed)).setTextColor(Color.WHITE);
			((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
			((TextView) findViewById(R.id.progress)).setTextColor(Color.WHITE);
		}
		
		final long repo_id = viewApk.getRepo_id();
		final String repo_string = category.equals(Category.ITEMBASED)?db.getItemBasedServer(viewApk.getRepo_id()):RepoUtils.split(db.getServer(viewApk.getRepo_id(),getIntent().getExtras().getBoolean("top", false)).url);
		((TextView) findViewById(R.id.app_store)).setText("Store: " +repo_string);
		try {
			((RatingBar) findViewById(R.id.ratingbar)).setRating(Float
					.parseFloat(viewApk.getRating()));
		} catch (Exception e) {
			((RatingBar) findViewById(R.id.ratingbar)).setRating(0);
		}
		
		if(category.equals(Category.ITEMBASED)){
			 webservicespath = "http://webservices.aptoide.com/";
		}else{
			webservicespath = db.getWebServicesPath(repo_id);
		}
//		Button serch_mrkt = (Button)findViewById(R.id.btmarket);
//		serch_mrkt.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setData(Uri.parse("market://details?id="+viewApk.getApkid()));
//				try{
//					startActivity(intent);
//				}catch (ActivityNotFoundException e){
//					Toast.makeText(context, getText(R.string.error_no_market), Toast.LENGTH_LONG).show();
//				}
//			}
//			
//		});
		
		((TextView) findViewById(R.id.versionInfo)).setText(getString(R.string.clear_dwn_title) + " " + viewApk.getDownloads() + " "+ getString(R.string.size)+" "+ viewApk.getSize() + "KB");
		((TextView) findViewById(R.id.version_label)).setText(getString(R.string.version) + " "+ viewApk.getVername());
		((TextView) findViewById(R.id.app_name)).setText(viewApk.getName());
		ImageLoader imageLoader = new ImageLoader(context, db);
		if(category.equals(Category.ITEMBASED)){
			imageLoader.DisplayImage(-1, db.getItemBasedBasePath(viewApk.getRepo_id())+viewApk.getIconPath(),(ImageView) findViewById(R.id.app_icon), context, false,(viewApk.getApkid()+"|"+viewApk.getVercode()).hashCode()+"");
		}else{
			imageLoader.DisplayImage(viewApk.getRepo_id(), viewApk.getIconPath(),(ImageView) findViewById(R.id.app_icon), context, getIntent().getExtras().getBoolean("top", false),(viewApk.getApkid()+"|"+viewApk.getVercode()).hashCode()+"");
		}
		
		
		
		Comments comments = new Comments(context,webservicespath);
		comments.getComments(repo_string, viewApk.getApkid(),viewApk.getVername(),(LinearLayout) findViewById(R.id.commentContainer), false);
		likes = new Likes(context, webservicespath);
		likes.getLikes(repo_string, viewApk.getApkid(), viewApk.getVername(),(ViewGroup) findViewById(R.id.likesLayout),(ViewGroup) findViewById(R.id.ratings));
		ItemBasedApks items = new ItemBasedApks(context,viewApk);
		
		items.getItems((LinearLayout) findViewById(R.id.itembasedapks_container));
		System.out.println("Md5: " + viewApk.getMd5());;
		findViewById(R.id.btinstall).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(scheduledDownloadChBox.isChecked()){
					db.insertScheduledDownload(viewApk.getApkid(), viewApk.getVercode(), viewApk.getVername(), (category.equals(Category.ITEMBASED)?db.getItemBasedBasePath(viewApk.getRepo_id()):db.getBasePath(viewApk.getRepo_id(),getIntent()
							.getExtras().getBoolean("top", false)))
							+ viewApk.getPath(),viewApk.getName(),viewApk.getMd5());
				}else{
				
				ViewCache cache = new ViewCache(viewApk.hashCode(), viewApk.getMd5());
				if(cache.isCached() && cache.hasMd5Sum() && cache.checkMd5()){
					try {
						serviceDownloadManager.callInstallApp(cache);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}else{
					download = new ViewDownloadManagement((category.equals(Category.ITEMBASED)?db.getItemBasedBasePath(viewApk.getRepo_id()):db.getBasePath(viewApk.getRepo_id(),getIntent()
							.getExtras().getBoolean("top", false)))	+ viewApk.getPath(), viewApk, cache);
					Button manage = (Button) findViewById(R.id.icon_manage);
					manage.setVisibility(View.GONE);
					manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							setupQuickActions(true, view);
						}
					});
					try {
						serviceDownloadManager.callStartDownloadAndObserve(download, serviceDownloadManagerCallback);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					findViewById(R.id.download_progress).setVisibility(View.VISIBLE);
					findViewById(R.id.icon_manage).setVisibility(View.VISIBLE);
					findViewById(R.id.downloading_name).setVisibility(View.INVISIBLE);
				}
				}
			}
		});
		
		findViewById(R.id.add_comment).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ApkInfo.this,AddComment.class);
				i.putExtra("apkid", viewApk.getApkid());
				i.putExtra("version", viewApk.getVername());
				i.putExtra("repo", repo_string);
				i.putExtra("webservicespath",  "http://webservices.aptoide.com/");
				startActivityForResult(i, AddComment.ADD_COMMENT_REQUESTCODE);
			}
		});
		
		findViewById(R.id.likesImage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postLike(EnumUserTaste.LIKE,repo_string);
				
			}
		});
		
		findViewById(R.id.dislikesImage).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postLike(EnumUserTaste.DONTLIKE,repo_string);
				
			}
		});
		
		
		
		findViewById(R.id.more_comments).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ApkInfo.this,ViewComments.class);
				i.putExtra("repo", repo_string);
				i.putExtra("apkid", viewApk.getApkid());
				i.putExtra("vername", viewApk.getVername());
				i.putExtra("webservicespath", "http://webservices.aptoide.com/");
				startActivity(i);
				
			}
		});
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					
					final ArrayList<String> originalList = new ArrayList<String>();;
				switch (category) {
				case TOP:
				case LATEST:
				case ITEMBASED:
					db.getScreenshots(originalList,viewApk,category);
					thumbnailList = new String[originalList.size()];
					
					for ( int i = 0; i!= originalList.size();i++){
						thumbnailList[i]=screenshotToThumb(originalList.get(i));
					}
					
					break;
				
				case INFOXML:
					String uri = webservicespath+"webservices/listApkScreens/"+repo_string+"/"+viewApk.getApkid()+"/"+viewApk.getVername()+"/json";
					JSONObject respJSON = NetworkUtils.getJsonObject(new URL(uri), ApkInfo.this);
					JSONArray imagesurl = respJSON.getJSONArray("listing");
					thumbnailList = new String[imagesurl.length()];
					for ( int i = 0; i!= imagesurl.length();i++){
						thumbnailList[i]=screenshotToThumb(imagesurl.getString(i));
					}

					for(int i=0;i < imagesurl.length();i++){ 
						originalList.add(imagesurl.getString(i));
					}
					break;
				default:
					break;
				}
					final CirclePageIndicator pi = (CirclePageIndicator) findViewById(R.id.indicator);
					final CustomViewPager screenshots = (CustomViewPager) findViewById(R.id.screenShotsPager);
					runOnUiThread(new Runnable() {

						public void run() {
							if(thumbnailList!=null&&thumbnailList.length>0){
								String hashCode = (viewApk.getApkid()+"|"+viewApk.getVercode()).hashCode()+"";
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,thumbnailList,originalList,hashCode));
								pi.setFillColor(Color.DKGRAY);
								pi.setViewPager(screenshots);
								pi.setRadius(6.5f);
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
									}
									
									public void onPageScrolled(int arg0, float arg1, int arg2) {
										
									}
									
									public void onPageScrollStateChanged(int arg0) {
										
									}
								});
							}
							
						}
					});
				} catch (Exception e){
					e.printStackTrace();
				}
				Cursor c = getContentResolver().query(ExtrasContentProvider.CONTENT_URI, new String[]{ExtrasDbOpenHelper.COLUMN_COMMENTS_COMMENT}, ExtrasDbOpenHelper.COLUMN_COMMENTS_APKID+"=?", new String[]{viewApk.getApkid()}, null);
				
				description_text = "";
				System.out.println(c.getCount());
				if(c.moveToFirst()){
					description_text = c.getString(0);
				}else{
					description_text=getString(R.string.no_descript);
				}
				
				c.close();
				
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
				
						final TextView description = (TextView) findViewById(R.id.descript);
						description.setText(description_text);
				if(description.getLineCount()>10){
					description.setMaxLines(10);
					findViewById(R.id.show_all_description).setVisibility(View.VISIBLE);
					findViewById(R.id.show_all_description).setOnClickListener(new OnClickListener() {
						
						
						@Override
						public void onClick(View v) {
							
							if(collapsed){
								collapsed=false;
								scrollPosition = (int)((ScrollView)findViewById(R.id.scrollView1)).getScrollY();
								description.setMaxLines(Integer.MAX_VALUE);
								((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_up, 0);
								((TextView) findViewById(R.id.show_all_description)).setText("Show Less");
							}else{
								collapsed=true;
								((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_down, 0);
								description.setMaxLines(10);
								((ScrollView)findViewById(R.id.scrollView1)).scrollTo(0, scrollPosition);
								((TextView) findViewById(R.id.show_all_description)).setText("Show More");
							}
						}
					});
					findViewById(R.id.description_container).setOnClickListener(new OnClickListener() {
						
						
						@Override
						public void onClick(View v) {
							
							if(collapsed){
								collapsed=false;
								scrollPosition = (int)((ScrollView)findViewById(R.id.scrollView1)).getScrollY();
								description.setMaxLines(Integer.MAX_VALUE);
								((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_up, 0);
								((TextView) findViewById(R.id.show_all_description)).setText("Show Less");
							}else{
								collapsed=true;
								((TextView)findViewById(R.id.show_all_description)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_arrow_down, 0);
								description.setMaxLines(10);
								((ScrollView)findViewById(R.id.scrollView1)).scrollTo(0, scrollPosition);
								((TextView) findViewById(R.id.show_all_description)).setText("Show More");
							}
						}
					});
				}
				
				
					}
				});
			}
			
			
		}).start();
		
	}
	
	
	String description_text;
	private boolean collapsed = true;
	int scrollPosition = 0;
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loadElements(id);
	}
	
	protected void postLike(EnumUserTaste like, String repo_string) {
		userTaste = like;
		if(Login.isLoggedIn(this)){
			try{
				likes.postLike(repo_string, viewApk.getApkid(), viewApk.getVername(), like);	
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			Intent i = new Intent(this,Login.class);
			startActivityForResult(i, Login.REQUESTCODE);
		}
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			Log.d("Aptoide-ApkInfo", "download status update: "+EnumDownloadStatus.reverseOrdinal(msg.what).name());
			ProgressBar progress;
			switch (EnumDownloadStatus.reverseOrdinal(msg.what)) {
				case PAUSED:
					progress = (ProgressBar) findViewById(R.id.downloading_progress);
					progress.setIndeterminate(false);
					progress.setProgress(download.getProgress());
					((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString(ApkInfo.this));
					((TextView) findViewById(R.id.speed)).setTextColor(Color.WHITE);
					((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
					((TextView) findViewById(R.id.progress)).setTextColor(Color.WHITE);
//					Log.d("ApkInfo-DownloadListener", "receiving: "+download);
					break;
	
				case RESUMING:
					progress = (ProgressBar) findViewById(R.id.downloading_progress);
					progress.setIndeterminate(false);
					progress.setProgress(download.getProgress());
					((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString(ApkInfo.this));
					((TextView) findViewById(R.id.speed)).setTextColor(Color.WHITE);
					((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
					((TextView) findViewById(R.id.progress)).setTextColor(Color.WHITE);
//					Log.d("ApkInfo-DownloadListener", "receiving: "+download);
					break;
					
				case DOWNLOADING:
	
					progress = (ProgressBar) findViewById(R.id.downloading_progress);
					progress.setIndeterminate(false);
					progress.setProgress(download.getProgress());
					((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString(ApkInfo.this));
					((TextView) findViewById(R.id.speed)).setTextColor(Color.WHITE);
					((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
					((TextView) findViewById(R.id.progress)).setTextColor(Color.WHITE);
//					Log.d("ApkInfo-DownloadListener", "receiving: "+download);
	
					break;
					
				case FAILED:
					Log.d("ApkInfo-DownloadListener", "Download Failed due to: "+download.getDownload().getFailReason().toString(getApplicationContext()));
					if(download.getDownload().getFailReason().equals(EnumDownloadFailReason.IP_BLACKLISTED)){
						new DialogIpBlacklisted(ApkInfo.this).show();
					}else{
						Toast.makeText(context, "Download Failed due to: "+download.getDownload().getFailReason().toString(getApplicationContext()), Toast.LENGTH_LONG).show();
					}
					findViewById(R.id.download_progress).setVisibility(View.GONE);
					findViewById(R.id.icon_manage).setVisibility(View.GONE);
					findViewById(R.id.downloading_name).setVisibility(View.GONE);
				case RESTARTING:
					break;
					
				case STOPPED:
				case COMPLETED:
					if(actionBar!=null){
						actionBar.dismiss();
					}
					findViewById(R.id.download_progress).setVisibility(View.GONE);
					findViewById(R.id.icon_manage).setVisibility(View.GONE);
					findViewById(R.id.downloading_name).setVisibility(View.GONE);
					break;
					
				default:
					break;
				}
			
		}
	};
	
	protected String screenshotToThumb(String string) {

		String[] splitedString = string.split("/");
		StringBuilder db = new StringBuilder();
		for (int i = 0; i != splitedString.length - 1; i++) {
			db.append(splitedString[i]);
			db.append("/");
		}
		db.append("thumbs/mobile/");
		db.append(splitedString[splitedString.length - 1]);

		return db.toString();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader loader = new SimpleCursorLoader(ApkInfo.this) {

			@Override
			public Cursor loadInBackground() {
				return db.getAllApkVersions(viewApk.getApkid(),viewApk.getId(),viewApk.getVername(), getIntent()
						.getExtras().getBoolean("top", false),viewApk.getRepo_id());
			}
		};
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
		if(arg1.getCount()>1){
			spinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!download.isNull()){
			try {
				serviceDownloadManager.callUnregisterDownloadObserver(viewApk.hashCode());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		unbindService(serviceManagerConnection);
		handler = null;
	}
	
	


	private QuickAction actionBar;
	private void setupQuickActions(boolean downloading, View view){
		actionBar  = new QuickAction(context);
		ActionItem playItem = new ActionItem(EnumQuickActions.PLAY.ordinal(), "Resume", context.getResources().getDrawable(R.drawable.ic_media_play));
		ActionItem pauseItem = new ActionItem(EnumQuickActions.PAUSE.ordinal(), "Pause", context.getResources().getDrawable(R.drawable.ic_media_pause));
		ActionItem stopItem = new ActionItem(EnumQuickActions.STOP.ordinal(), "Stop", context.getResources().getDrawable(R.drawable.ic_media_stop));
		
		switch (download.getDownloadStatus()) {
			case SETTING_UP:
			case RESTARTING:
			case RESUMING:
				break;
				
			case DOWNLOADING:
				actionBar.addActionItem(pauseItem);
				break;
	
			default:
				actionBar.addActionItem(playItem);
				break;
		}
		actionBar.addActionItem(stopItem);
		actionBar.show(view);
		
		actionBar.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
				try {
					switch (EnumQuickActions.reverseOrdinal(actionId)) {
						case PLAY:
							serviceDownloadManager.callResumeDownload(download.hashCode());
							break;
							
						case PAUSE:
							serviceDownloadManager.callPauseDownload(download.hashCode());
							break;
							
						case STOP:
							serviceDownloadManager.callStopDownload(download.hashCode());
							break;

						default:
							break;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}	
			}
		});		
	}
	
}
