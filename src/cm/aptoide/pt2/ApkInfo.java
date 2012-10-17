package cm.aptoide.pt2;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt2.adapters.ViewPagerAdapterScreenshots;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt2.util.RepoUtils;
import cm.aptoide.pt2.util.quickaction.ActionItem;
import cm.aptoide.pt2.util.quickaction.EnumQuickActions;
import cm.aptoide.pt2.util.quickaction.QuickAction;
import cm.aptoide.pt2.views.EnumDownloadProgressUpdateMessages;
import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownloadManagement;
import cm.aptoide.pt2.views.ViewLogin;
import cm.aptoide.pt2.webservices.comments.Comments;
import cm.aptoide.pt2.webservices.taste.Likes;

public class ApkInfo extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	
	private ViewApk viewApk = null;
	private Database db;
	private Spinner spinner;
	SimpleCursorAdapter adapter;
	long id;
	Category category;
	Activity context;
	boolean spinnerInstaciated = false;
	private ViewDownloadManagement download;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.apk_info);
		category = Category.values()[getIntent().getIntExtra("category", 0)];
		context = this;
		db = Database.getInstance(this);
		id = getIntent().getExtras().getLong("_id");
		loadElements(id);
		// apkVersions = db.getAllApkVersions(viewApk.getApkid());
		if(!category.equals(Category.ITEMBASED)){
			spinner = (Spinner) findViewById(R.id.spinnerMultiVersion);
			spinner.setVisibility(View.VISIBLE);
			adapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_spinner_item, null,
					new String[] { "vername" ,"repo_id"}, new int[] { android.R.id.text1 },
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			adapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
					((TextView) arg0).setText("Version " + arg1.getString(arg2) +" - "+RepoUtils.split(db.getServer(arg1.getLong(3)).url));
					System.out.println("repo_id="+arg1.getString(3));
					return true;
				}
			});
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (spinnerInstaciated) {
						if(!download.isNull()){
							download.unregisterObserver(viewApk.hashCode());
						}
						loadElements(arg3);
					} else {
						spinnerInstaciated = true;
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
	private void loadElements(long id) {
		findViewById(R.id.downloading_icon).setVisibility(View.GONE);
		findViewById(R.id.downloading_name).setVisibility(View.GONE);
		findViewById(R.id.download_progress).setVisibility(View.GONE);
		ProgressBar progress = (ProgressBar) findViewById(R.id.downloading_progress);
		progress.setIndeterminate(true);
		Button manage = (Button) findViewById(R.id.icon_manage);
		manage.setVisibility(View.GONE);
		manage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				quickAction.show(view);
			}
		});
		setupQuickActions();
		
		System.out.println("loading " + id + " " +category.name());
		if(category.equals(Category.ITEMBASED)){
			viewApk = db.getItemBasedApk(id);
		}else{
			viewApk = db.getApk(id, getIntent().getExtras().getBoolean("top", false));
		}
		
		
		download = ((ApplicationServiceManager)getApplication()).getAppDownloading(viewApk.hashCode());
		if(!download.isNull()){
			download.registerObserver(viewApk.hashCode(),handler);
			findViewById(R.id.download_progress).setVisibility(View.VISIBLE);
			findViewById(R.id.icon_manage).setVisibility(View.VISIBLE);
			findViewById(R.id.downloading_name).setVisibility(View.INVISIBLE);
			((ProgressBar) findViewById(R.id.downloading_progress)).setProgress(download.getProgress());
			((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString());
			((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
		}
		
		final long repo_id = viewApk.getRepo_id();
		final String repo_string = category.equals(Category.ITEMBASED)?db.getItemBasedServer(viewApk.getRepo_id()):RepoUtils.split(db.getServer(viewApk.getRepo_id()).url);
		((TextView) findViewById(R.id.app_store)).setText(repo_string);
		try {
			((RatingBar) findViewById(R.id.rating)).setRating(Float
					.parseFloat(viewApk.getRating()));
		} catch (Exception e) {
			((RatingBar) findViewById(R.id.rating)).setRating(0);
		}
		
		if(category.equals(Category.ITEMBASED)){
			 webservicespath = "http://webservices.aptoide.com/";
		}else{
			webservicespath = db.getWebServicesPath(repo_id);
		}
		
		((TextView) findViewById(R.id.versionInfo)).setText("Downloads: " + viewApk.getDownloads() + " Size: "+ viewApk.getSize() + "KB");
		((TextView) findViewById(R.id.version_label)).setText("Version: "+ viewApk.getVername());
		((TextView) findViewById(R.id.app_name)).setText(viewApk.getName());
		ImageLoader imageLoader = new ImageLoader(context, db);
		if(category.equals(Category.ITEMBASED)){
			imageLoader.DisplayImage(-1, db.getItemBasedBasePath(viewApk.getRepo_id())+viewApk.getIconPath(),(ImageView) findViewById(R.id.app_hashid), context, false, viewApk.hashCode()+"");
		}else{
			imageLoader.DisplayImage(viewApk.getRepo_id(), viewApk.getIconPath(),(ImageView) findViewById(R.id.app_hashid), context, false, viewApk.hashCode()+"");
		}
		
		
		
		Comments comments = new Comments(context,webservicespath);
		comments.getComments(repo_string, viewApk.getApkid(),viewApk.getVername(),(LinearLayout) findViewById(R.id.commentContainer), false);
		Likes likes = new Likes(context, webservicespath);
		likes.getLikes(repo_string, viewApk.getApkid(), viewApk.getVername(),(ViewGroup) findViewById(R.id.likesLayout));
		ItemBasedApks items = new ItemBasedApks(context,viewApk);
		
		items.getItems((LinearLayout) findViewById(R.id.itembasedapks_container));
		
		findViewById(R.id.btinstall).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewCache cache = new ViewCache(viewApk.hashCode(), viewApk.getMd5());
				if(cache.isCached() && cache.hasMd5Sum() && cache.checkMd5()){
					((ApplicationServiceManager)getApplication()).installApp(cache);
				}else{
					download = new ViewDownloadManagement((ApplicationServiceManager) getApplication(), (category.equals(Category.ITEMBASED)?db.getItemBasedBasePath(viewApk.getRepo_id()):db.getBasePath(viewApk.getRepo_id()))
							+ viewApk.getPath(), viewApk, cache);
					
					download.registerObserver(viewApk.hashCode(),handler);
					download.startDownload();
					findViewById(R.id.download_progress).setVisibility(View.VISIBLE);
					findViewById(R.id.icon_manage).setVisibility(View.VISIBLE);
					findViewById(R.id.downloading_name).setVisibility(View.INVISIBLE);
				}
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
					db.getScreenshots(originalList,viewApk,category);
					thumbnailList = new String[originalList.size()];
					
					for ( int i = 0; i!= originalList.size();i++){
						thumbnailList[i]=screenshotToThumb(originalList.get(i));
					}
					break;
				case ITEMBASED:
				case INFOXML:
					String uri = webservicespath+"webservices/listApkScreens/"+repo_string+"/"+viewApk.getApkid()+"/"+viewApk.getVername()+"/json";
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
					HttpResponse response=null;
					HttpGet request = new HttpGet();
				
						request.setURI(new URI(uri));
						System.out.println(request.getURI());
						response = client.execute(request);
						System.out.println(request.getURI()+"");
						String temp = EntityUtils.toString(response.getEntity());
						JSONObject respJSON = new JSONObject(temp);
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
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,thumbnailList,originalList, viewApk.hashCode()));
								
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
			}
		}).start();
		
	}
	
	
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch (EnumDownloadProgressUpdateMessages.reverseOrdinal(msg.what)) {
			case UPDATE:
			case PAUSED:
			case RESUMING:
				ProgressBar progress = (ProgressBar) findViewById(R.id.downloading_progress);
				progress.setIndeterminate(false);
				progress.setProgress(download.getProgress());
				((TextView) findViewById(R.id.speed)).setText(download.getSpeedInKBpsString());
				((TextView) findViewById(R.id.progress)).setText(download.getProgressString());
				Log.d("ApkInfo-DownloadListener", "receiving: "+download);
				break;
			case FAILED:
				Toast.makeText(context, "Download Failed due to: "+download.getDownload().getFailReason().toString(getApplicationContext()), Toast.LENGTH_LONG).show();
			case RESTARTING:
				break;
			case STOPPED:
			case COMPLETED:
				findViewById(R.id.download_progress).setVisibility(View.GONE);
				findViewById(R.id.icon_manage).setVisibility(View.GONE);
				findViewById(R.id.downloading_name).setVisibility(View.GONE);
				break;
			default:
				break;
			}
			
		};
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
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!download.isNull()){
			download.unregisterObserver(viewApk.hashCode());
		}
		handler = null;
	}
	
	


	private QuickAction quickAction;
	private void setupQuickActions(){
		quickAction  = new QuickAction(context);
		
		ActionItem playItem = new ActionItem(EnumQuickActions.PLAY.ordinal(), "Resume", context.getResources().getDrawable(android.R.drawable.ic_media_play));
		ActionItem pauseItem = new ActionItem(EnumQuickActions.PAUSE.ordinal(), "Pause", context.getResources().getDrawable(android.R.drawable.ic_media_pause));
		ActionItem stopItem = new ActionItem(EnumQuickActions.STOP.ordinal(), "Stop", context.getResources().getDrawable(R.drawable.ic_media_stop));
		
		quickAction.addActionItem(playItem);
		quickAction.addActionItem(pauseItem);
		quickAction.addActionItem(stopItem);

		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
				switch (EnumQuickActions.reverseOrdinal(actionId)) {
					case PLAY:
						download.resume();
						break;
						
					case PAUSE:
						download.pause();
						break;
						
					case STOP:
						download.stop();
						break;
	
					default:
						break;
				}	
			}
		});		
	}
	
}
