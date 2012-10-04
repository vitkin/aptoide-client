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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import cm.aptoide.pt2.adapters.ViewPagerAdapterScreenshots;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt2.util.RepoUtils;
import cm.aptoide.pt2.views.EnumCacheType;
import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownloadManagement;
import cm.aptoide.pt2.webservices.comments.Comments;
import cm.aptoide.pt2.webservices.taste.Likes;

public class ApkInfo extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private ViewApk viewApk = null;
	private Database db;
	private Spinner spinner;
	SimpleCursorAdapter adapter;
	long id;
	Activity context;
	boolean spinnerInstaciated = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.apk_info);
		context = this;
		db = Database.getInstance(this);
		id = getIntent().getExtras().getLong("_id");
		loadElements(id);
		// apkVersions = db.getAllApkVersions(viewApk.getApkid());
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

	private void loadElements(long id) {
		System.out.println("loading " + id);
		viewApk = db.getApk(id, getIntent().getExtras()
				.getBoolean("top", false));
		final long repo_id = viewApk.getRepo_id();
		final String repo_string = RepoUtils.split(db.getServer(viewApk.getRepo_id()).url);
		((TextView) findViewById(R.id.app_store)).setText(repo_string);
		try {
			((RatingBar) findViewById(R.id.rating)).setRating(Float
					.parseFloat(viewApk.getRating()));
		} catch (Exception e) {
			((RatingBar) findViewById(R.id.rating)).setRating(0);
		}
		((TextView) findViewById(R.id.versionInfo))
				.setText("Downloads: " + viewApk.getDownloads() + " Size: "
						+ viewApk.getSize() + "KB");
		((TextView) findViewById(R.id.version_label)).setText("Version: "
				+ viewApk.getVername());
		((TextView) findViewById(R.id.app_name)).setText(viewApk.getName());
		ImageLoader imageLoader = new ImageLoader(context, db);
		imageLoader.DisplayImage(viewApk.getRepo_id(), viewApk.getIconPath(),
				(ImageView) findViewById(R.id.app_hashid), context, false);
		Comments comments = new Comments(context,
				db.getWebServicesPath(repo_id));
		comments.getComments(repo_string, viewApk.getApkid(),
				viewApk.getVername(),
				(LinearLayout) findViewById(R.id.commentContainer), false);
		Likes likes = new Likes(context, db.getWebServicesPath(repo_id));
		likes.getLikes(repo_string, viewApk.getApkid(), viewApk.getVername(),
				(ViewGroup) findViewById(R.id.likesLayout));
		ItemBasedApks items = new ItemBasedApks(context,viewApk);
		
		items.getItems(findViewById(R.id.itembasedapks_container));
		
		findViewById(R.id.btinstall).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ViewDownloadManagement(
						(ApplicationServiceManager) getApplication(), db
								.getBasePath(viewApk.getRepo_id())
								+ viewApk.getPath(), viewApk, new ViewCache(
								EnumCacheType.APK, viewApk.getId()))
						.startDownload();
			}
		});
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String uri = db.getWebServicesPath(repo_id)+"webservices/listApkScreens/"+repo_string+"/"+viewApk.getApkid()+"/"+viewApk.getVername()+"/json";
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
				HttpResponse response=null;
				HttpGet request = new HttpGet();
				try{
					request.setURI(new URI(uri));
					System.out.println(request.getURI());
					response = client.execute(request);
					System.out.println(request.getURI()+"");
					String temp = EntityUtils.toString(response.getEntity());
					JSONObject respJSON = new JSONObject(temp);
					JSONArray imagesurl = respJSON.getJSONArray("listing");
					final String[] thumbnailList = new String[imagesurl.length()];
					for ( int i = 0; i!= imagesurl.length();i++){
						thumbnailList[i]=screenshotToThumb(imagesurl.getString(i));
					}
					final ArrayList<String> originalList = new ArrayList<String>();
					for(int i=0;i < imagesurl.length();i++){ 
						originalList.add(imagesurl.getString(i));
					}
					final CirclePageIndicator pi = (CirclePageIndicator) findViewById(R.id.indicator);
					final CustomViewPager screenshots = (CustomViewPager) findViewById(R.id.screenShotsPager);
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
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		
		
	}
	
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
				return db.getAllApkVersions(viewApk.getApkid(), getIntent()
						.getExtras().getBoolean("top", false));
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

}
