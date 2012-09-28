package cm.aptoide.pt2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
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
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt2.util.RepoUtils;
import cm.aptoide.pt2.views.EnumCacheType;
import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownload;
import cm.aptoide.pt2.views.ViewDownloadManagement;
import cm.aptoide.pt2.webservices.comments.Comments;
import cm.aptoide.pt2.webservices.taste.Likes;

public class ApkInfo extends FragmentActivity implements LoaderCallbacks<Cursor>{

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
//		apkVersions = db.getAllApkVersions(viewApk.getApkid());
		spinner = (Spinner) findViewById(R.id.spinnerMultiVersion);
		spinner.setVisibility(View.VISIBLE);
		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, new String[]{"vername"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
				((TextView) arg0).setText("Version " + arg1.getString(arg2));
				return true;
			}
		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spinnerInstaciated){
					loadElements(arg3);
				}else{
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
		System.out.println("loading "+id);
		viewApk = db.getApk(id,getIntent().getExtras().getBoolean("top",false));
		long repo_id = viewApk.getRepo_id();
		String repo_string = db.getServer(viewApk.getRepo_id()).url;
		((TextView) findViewById(R.id.app_store)).setText(repo_string);
		try{
			((RatingBar) findViewById(R.id.rating)).setRating(Float.parseFloat(viewApk.getRating()));
		}catch (Exception e) {
			((RatingBar) findViewById(R.id.rating)).setRating(0);
		}
		((TextView) findViewById(R.id.versionInfo)).setText("Downloads: "+viewApk.getDownloads() + " Size:" +viewApk.getSize());
		((TextView) findViewById(R.id.version_label)).setText(viewApk.getVername());
		((TextView) findViewById(R.id.app_name)).setText(viewApk.getName());
		ImageLoader imageLoader = new ImageLoader(context, db);
		imageLoader.DisplayImage(viewApk.getRepo_id(),viewApk.getIconPath() , (ImageView) findViewById(R.id.app_hashid), context, false);
		repo_string = RepoUtils.split(repo_string);
		Comments comments = new Comments(context, db.getWebServicesPath(repo_id));
		comments.getComments(repo_string, viewApk.getApkid(), viewApk.getVername(), (LinearLayout)findViewById(R.id.commentContainer), false);
		Likes likes = new Likes(context, db.getWebServicesPath(repo_id));
		likes.getLikes(repo_string, viewApk.getApkid(), viewApk.getVername(), (ViewGroup) findViewById(R.id.likesLayout));
		findViewById(R.id.btinstall).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new ViewDownloadManagement((ApplicationServiceManager) getApplication(), db.getBasePath(viewApk.getRepo_id())+viewApk.getPath(), viewApk, new ViewCache(EnumCacheType.APK, viewApk.getId())).startDownload();
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader loader = new SimpleCursorLoader(ApkInfo.this) {
			
			@Override
			public Cursor loadInBackground() {
				return db.getAllApkVersions(viewApk.getApkid(),getIntent().getExtras().getBoolean("top",false));
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
