package cm.aptoide.pt;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import cm.aptoide.pt.data.volatil.EnumUserTaste;

public class AppInfo extends Activity{// implements OnDismissListener{
	
	private SharedPreferences sharedPreferences;
	
	private Intent apkinfo = null;
	private Context mctx = null;
//	private DbHandler db;
	private Intent rtrn_intent = null;
	
	private boolean jback = false;
	
	/*private Integer[] imageIDs = {
			R.drawable.no_screen
	};*/
	
	private Drawable[] imageDrwb = null;
	
	//private Gallery galry = null;
	
	private String apk_name_str = null;
	private TextView noscreens = null;
	private LinkedList<ImageView> screens = null;
	
	private Gallery galleryView = null;
	
	private Spinner spinnerMulti;
//	private CommentsAdapter<Comment> commentAdapter;
//	private CommentPosterListOnScrollListener loadOnScrollCommentList;
	private String apk_repo_str;
	private String apk_id;
	private CheckBox checkbox;
	private ImageView like;
	private ImageView dislike;
	private TextView likes;
	private TextView dislikes;
	private String apk_repo_str_raw;
	private String apk_ver_str_raw;
	private EnumUserTaste taste;
//	private WrapperUserTaste userTaste;
//	private TastePoster tastePoster;
//	private VersionApk versionInstApk;
//	private GetScreenShots previousGetter;
	
	private static int headers = 2; // The number of header items on the list view
	
	/**
	 * @author rafael
	 * @since 2.5.3
	 * 
	 */
//	class GetScreenShots extends Thread{
//		
//		private String version;
//		private AtomicBoolean canceled;
//		
//		public GetScreenShots(String version){
//			this.version 	= version;
//			canceled 		= new AtomicBoolean(false);
//		}
//		
//		public void run(){
//			try{
//				
//				
//				String ws_repo = apk_repo_str.substring(7).split("[\\.]")[0];
//				String fetch_imgs = Configs.WEB_SERVICE_SCREENS_LIST+ws_repo+"/"+apk_id+"/"+version+"/json";
//
//				Log.d("Aptoide",apk_repo_str + " vs " + ws_repo);
//				Log.d("Aptoide","Get img from: " + fetch_imgs);
//				
//				HttpResponse response_ws = NetworkApis.imgWsGet(fetch_imgs);
//				if(response_ws != null && response_ws.getStatusLine().getStatusCode() == 200){
//					String json_str = null;
//					json_str = EntityUtils.toString(response_ws.getEntity());
//					response_ws.getEntity().consumeContent();
//					Log.d("Aptoide","Resp: " + json_str);
//					JSONObject json_resp = new JSONObject(json_str);
//					
//					JSONArray img_url = json_resp.getJSONArray("listing");
//					if(img_url.length()>0)
//						imageDrwb = new Drawable[img_url.length()];
//					for(int i = 0; i< img_url.length(); i++){
//						
//						if(canceled.get())
//							throw new CancelledScreenShots();
//						
//						String a = (String)img_url.get(i);
//						Log.d("Aptoide","* " + a);
//						HttpResponse pic = NetworkApis.imgWsGet(a);
//						InputStream pic_st = pic.getEntity().getContent();
//						//TODO fix java.lang.OutOfMemoryError: bitmap size exceeds VM budget
//						try{
//							Drawable pic_drw = Drawable.createFromStream(pic_st, "src"); //hear
//							imageDrwb[i] = pic_drw;
//						}catch(OutOfMemoryError e){}
//						
//					}
//				}
//				
//			}catch (Exception e ){	
//			}finally{
//				if(!canceled.get())
//					updateScreenshots.sendEmptyMessage(0);
//			}
//			
//		}
//		
//		public void cancel(){
//			canceled.set(true);
//		}
//		
//		@SuppressWarnings("serial")
//		class CancelledScreenShots extends Exception{}
//		
//	}
	
	/**
	 * @author rafael
	 * @since 2.5.3
	 * 
	 */
//	public class WrapperUserTaste{
//		
//		private EnumUserTaste userTaste;
//		private int operatingThreads;
//		public WrapperUserTaste(){userTaste=EnumUserTaste.NOTEVALUATED; operatingThreads=0;}
//		public EnumUserTaste getValue(){ return userTaste; } 
//		public void setValue(EnumUserTaste userTaste){ this.userTaste = userTaste; }
//		public void incOperatingThreads(){ operatingThreads++; }
//		public void decOperatingThreads(){ 
//			if(operatingThreads!=0)
//				operatingThreads--;
//		}
//		public int getOperatingThreads(){ return operatingThreads; }
//		
//	}
	
	/**
	 * @author rafael
	 * @since 2.5.3
	 * 
	 */
	public enum Event{
		REPLY(0), COPY_TO_CLIPBOARD(1);
		private int id;
		private String string;
		
		private Event(int id){
			this.id = Menu.FIRST+id;
			this.string=null;
		}
		public int getId() {
			return id;
		}
		public String getString() {
			return string;
		}
		public void setString(String string) {
			this.string = string;
		}
		public static Event getEventFromId(int id){
			for(Event event:values()){
				if(event.getId()==id)
					return event;
			}
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		super.onCreate(savedInstanceState);
		apkinfo = getIntent();
//		db = new DbHandler(this);
		setContentView(R.layout.apkinfo);
		
		sharedPreferences = AppInfo.this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		
//		Event.REPLY.setString(this.getString(R.string.reply));
//		Event.COPY_TO_CLIPBOARD.setString(this.getString(R.string.copyclip));
		ListView listView = (ListView) findViewById(R.id.listComments);
		LayoutInflater inflater = this.getLayoutInflater();
		final LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.header_comments,listView, false);
		
//		ArrayList<VersionApk> versions = apkinfo.getParcelableArrayListExtra("versions");
//		Collections.sort(versions, Collections.reverseOrder());
		
		this.likes = (TextView)linearLayout.findViewById(R.id.likes);
		this.dislikes = (TextView)linearLayout.findViewById(R.id.dislikes);
		
		this.like = ((ImageView)linearLayout.findViewById(R.id.likesImage));
		this.dislike = ((ImageView)linearLayout.findViewById(R.id.dislikesImage));
		
//		this.userTaste = new WrapperUserTaste();
		this.taste = EnumUserTaste.NOTEVALUATED;
//		tastePoster = null;
		galleryView = (Gallery) linearLayout.findViewById(R.id.galleryScreens);
		
//		updateScreenshots = new ScreenShotsUpdate(linearLayout);
//		resetScreenshots = new ResetScreenshots(linearLayout);
//		previousGetter = null;
		
		checkbox= (CheckBox) linearLayout.findViewById(R.id.schedule_download_box);
		
		
		
		mctx = this;
		
		
		
		screens = new LinkedList<ImageView>();
		
		noscreens = (TextView) linearLayout.findViewById(R.id.noscreens);
		
		rtrn_intent = new Intent();
		
		
		/**************** TODO fill data *********************************/
//		apk_id = apkinfo.getStringExtra("apk_id");
//		final int type = apkinfo.getIntExtra("type", 0);
//		
//		
//		versionInstApk = (VersionApk) apkinfo.getParcelableExtra("instversion");
//		String icon_path = apkinfo.getStringExtra("icon");
//		apk_name_str = apkinfo.getStringExtra("name");
//		String apk_descr = apkinfo.getStringExtra("about");
//		apk_repo_str = apkinfo.getStringExtra("server");
//		String apk_rat_str = apkinfo.getStringExtra("rat");
//		
		
		
		
		
		Button serch_mrkt = (Button)findViewById(R.id.btn_market);
		serch_mrkt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+apk_id));
				try{
					startActivity(intent);
				}catch (ActivityNotFoundException e){
//					Toast.makeText(mctx, getText(R.string.error_no_market), Toast.LENGTH_LONG).show();
				}
			}
			
		});
		final Button action = (Button) findViewById(R.id.btn1);
//		switch (type) {
//		case 0:
//			action.setText(getString(R.string.install));
//			break;
//
//		case 1:
//			action.setText(getString(R.string.rem));
//			break;
//			
//		case 2:
//			action.setText(getString(R.string.update));
//			break;
//		}
		
		spinnerMulti = ((Spinner)linearLayout.findViewById(R.id.spinnerMultiVersion));
		
		action.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				int pos = apkinfo.getIntExtra("position", -1);
				
//				switch (type) {
//				case 0:
//					if(checkbox.isChecked()){
//						if(!db.existScheduledDownload(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion())){
//							db.insertScheduled(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
//							Toast.makeText(mctx, R.string.addSchDown, Toast.LENGTH_LONG).show();
//						}
//					}else{
//					rtrn_intent.putExtra("apkid", apk_id);
//					rtrn_intent.putExtra("in", true);
//					rtrn_intent.putExtra("position", pos);
//					jback = true;
//					}
//					break;
//
//				case 1:
//					
//					if(checkbox.isChecked()){
//						if(!db.existScheduledDownload(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion())){
//							db.insertScheduled(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
//							Toast.makeText(mctx, R.string.addSchDown, Toast.LENGTH_LONG).show();
//						}
//					}else{
//					rtrn_intent.putExtra("apkid", apk_id);
//					
//					rtrn_intent.putExtra("rm", false);
//					rtrn_intent.putExtra("install", true);
//					rtrn_intent.putExtra("in", true);
//					
//					rtrn_intent.putExtra("position", pos);
//					
//					jback = true;
//					}
//					break;
//
//				case 2:
//					
//					if(checkbox.isChecked()){
//						
//						if(!db.existScheduledDownload(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion())){
//							db.insertScheduled(apk_id, ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
//							Toast.makeText(mctx, R.string.addSchDown, Toast.LENGTH_LONG).show();
//						}
//					}else{
//					rtrn_intent.putExtra("apkid", apk_id);
//					jback = true;
//					}
//					break;
//				}
				
				
//				rtrn_intent.putExtra("version", ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
				
				
				finish();
				
			}
		});
		
		ImageView icon = (ImageView) findViewById(R.id.appicon);
//		File test_icon = new File(icon_path);
		
		
//		if(test_icon.exists() && test_icon.length() > 0){
//			icon.setImageDrawable(new BitmapDrawable(icon_path));
//		}else{
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
//		}
		
		TextView apk_name = (TextView)findViewById(R.id.app_name);
		apk_name.setText(apk_name_str);
		
		TextView apk_about = (TextView)linearLayout.findViewById(R.id.descript);
//		String desc_parsed = Html.fromHtml(apk_descr).toString();
//		apk_about.setText(desc_parsed);
		
		final TextView apk_repo = (TextView)findViewById(R.id.app_repo);
		apk_repo.setText(apk_repo_str);
		
		TextView apk_version = (TextView)findViewById(R.id.app_ver);
		
//		if(type == 1 || type == 2){
//			apk_version.setText(this.getString(R.string.version_inst)+": " + ( versionInstApk!=null?versionInstApk.getVersion(): "Not available" ) );
//		}else{
			apk_version.setVisibility(View.INVISIBLE);
//		}
		
		
		RatingBar apk_rat_n = (RatingBar) findViewById(R.id.rating);
//		apk_rat_n.setRating(new Float(apk_rat_str));
		
//		if(versions.size()!=0){
//			apk_ver_str_raw = versions.get(0).getVersion(); 
//		}else{
			apk_ver_str_raw = null;
//		}
		
//		apk_repo_str_raw 	= apk_repo_str.substring("http://".length(),apk_repo_str.indexOf(".bazaarandroid.com"));
		
		listView.addHeaderView(linearLayout, null, false);
//		commentAdapter = new CommentsAdapter<Comment>(this, R.layout.commentlistviewitem, new ArrayList<Comment>());
		
		
		/*Comments*/
//		if(Configs.COMMENTS_ON){
//			
//			if(Configs.COMMENTS_ADD_ON && versions.size()!=0){
//				TextView textView = new TextView(this);
//				textView.setText(this.getString(R.string.commentlabel));
//				textView.setTextSize(20);
//				textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//				textView.setPadding(0, 10, 0, 10);
//				textView.setGravity(Gravity.CENTER_HORIZONTAL);
//				listView.addHeaderView(textView);
//			}else{ headers--; }
//			LinearLayout loadComLayout = null;
//			if(versions.size()!=0){
//				loadComLayout = (LinearLayout) inflater.inflate(R.layout.loadingfootercomments,listView, false);
//				listView.addFooterView(loadComLayout);
//			}
//				listView.setAdapter(commentAdapter);
//				try {
//					if(versions.size()!=0){
//						loadOnScrollCommentList = new CommentPosterListOnScrollListener(this, commentAdapter, apk_repo_str_raw, apk_id, apk_ver_str_raw, loadComLayout);
//						listView.setOnScrollListener(loadOnScrollCommentList);
//					}
//				} 
//				//catch (ParserConfigurationException e) 	{} 
//				//catch (SAXException e) 					{}
//				catch(Exception e)							{}
//			
//			
//			listView.setOnItemClickListener(new OnItemClickListener(){
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					if(position==1){ // If comment app... option selected
//						Dialog commentDialog = new AddCommentDialog(ApkInfo.this, loadOnScrollCommentList, null, like, dislike, 
//								apk_repo_str_raw,
//				 				apk_id, 
//				 				apk_ver_str_raw,
//				 				userTaste);
//						commentDialog.show();
//					}
//				}
//			});
//			
//			registerForContextMenu(listView);
//		} else {
//			listView.setAdapter(commentAdapter);
//		}
		
		/*Multiversion*/
//			final Runnable newVersionFetchComments = new Runnable(){
//				public void run() {
//					loadOnScrollCommentList.fetchNewApp(apk_repo_str_raw, apk_id, apk_ver_str_raw);
//				}
//			};
			
			
			
//			final MultiversionSpinnerAdapter<VersionApk> spinnerMultiAdapter 
//				= new MultiversionSpinnerAdapter<VersionApk>(this, R.layout.textviewfocused, versions);
//			spinnerMultiAdapter.setDropDownViewResource(R.layout.multiversionspinneritem);
//			spinnerMulti.setAdapter(spinnerMultiAdapter );
//			if(type==2){
//				//Tab updates
//				spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener(){
//					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//							VersionApk versionApk = ((VersionApk)spinnerMultiAdapter.getItem(position));
//							apk_ver_str_raw = versionApk.getVersion();
//							if(Configs.TASTE_ON){
//								selectTaste(apk_repo_str_raw , apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
//							}
//							if(Configs.COMMENTS_ON && loadOnScrollCommentList!=null){
//								new Thread(newVersionFetchComments).start();
//							}
//							
//							if(previousGetter!=null)
//								previousGetter.cancel();
//							resetScreenshots.sendEmptyMessage(0);
//							
//							((TextView)ApkInfo.this.findViewById(R.id.versionInfo)).setText(MultiversionSpinnerAdapter.formatInfo((VersionApk)spinnerMulti.getSelectedItem()));
//							
//							previousGetter = new GetScreenShots(apk_ver_str_raw);
//							previousGetter.start();
//							
//					}
//					public void onNothingSelected(AdapterView<?> parent) {}
//				});
//				
//			} else if(type==1){
//				
//				Button uninstall = (Button)findViewById(R.id.btnUninstall);
//				uninstall.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, uninstall.getLayoutParams().height, 0.5f));
//				uninstall.setOnClickListener(new OnClickListener(){
//	
//						public void onClick(View arg0) {
//							
//							rtrn_intent.putExtra("apkid", apk_id);
//							
//							rtrn_intent.putExtra("rm", true);
//							rtrn_intent.putExtra("install", false);
//							
//							rtrn_intent.putExtra("position", apkinfo.getIntExtra("position", -1));
//							
//							jback = true;
//							finish();
//							
//						}
//						
//					});
//				
//				if(versions.size()!=0){
//					
//					spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener(){
//						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//							
//								VersionApk versionApk = ((VersionApk)spinnerMultiAdapter.getItem(position));
//								apk_ver_str_raw = versionApk.getVersion();
//								int result = versionApk.compareTo(versionInstApk);
//								
//								if(Configs.TASTE_ON){
//									selectTaste(apk_repo_str_raw , apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
//								}
//								if(Configs.COMMENTS_ON && loadOnScrollCommentList!=null){
//									new Thread(newVersionFetchComments).start();
//								}
//								
//								if(result==0){
//									action.setText("Reinstall");
//								}else if(result<0) {
//									action.setText(R.string.downgrade);
//								}else if(result>0) {
//									action.setText(R.string.update);
//								}
//								
//								if(previousGetter!=null){
//									previousGetter.cancel();
//								}
//								resetScreenshots.sendEmptyMessage(0);
//								
//								((TextView)ApkInfo.this.findViewById(R.id.versionInfo)).setText(MultiversionSpinnerAdapter.formatInfo((VersionApk)spinnerMulti.getSelectedItem()));
//								
//								previousGetter = new GetScreenShots(apk_ver_str_raw);
//								previousGetter.start();
//								
//						}
//						public void onNothingSelected(AdapterView<?> parent) {}
//					});
//					
//				}else{
//					//Otherwise
//					spinnerMulti.setVisibility(View.GONE);
//					dislikes.setVisibility(View.GONE);
//					like.setVisibility(View.GONE);
//					dislike.setVisibility(View.GONE);
//					((Button)findViewById(R.id.btn1)).setVisibility(View.GONE);
//					new GetScreenShots(apk_ver_str_raw).start();
//				}
//				
//			} else if(type==0){
//				
//				//If we are in tab available
//				spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener(){
//					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//						apk_ver_str_raw = ((VersionApk)spinnerMultiAdapter.getItem(position)).getVersion();
//						if(Configs.TASTE_ON){
//							selectTaste(apk_repo_str_raw , apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
//						}
//						if(Configs.COMMENTS_ON && loadOnScrollCommentList!=null){
//							new Thread(newVersionFetchComments).start();
//						}
//						
//						if(previousGetter!=null)
//							previousGetter.cancel();
//						resetScreenshots.sendEmptyMessage(0);
//						
//						((TextView)ApkInfo.this.findViewById(R.id.versionInfo)).setText(MultiversionSpinnerAdapter.formatInfo((VersionApk)spinnerMulti.getSelectedItem()));
//						
//						previousGetter = new GetScreenShots(apk_ver_str_raw);
//						previousGetter.start();
//					}
//					public void onNothingSelected(AdapterView<?> parent) {}
//				});
//			}
		
		
		
		
		
		/*Taste*/
//		if(Configs.TASTE_ON){
//			
//			if(Configs.TASTE_ADD_ON){
//			this.like.setOnTouchListener(new OnTouchListener(){
//			      public boolean onTouch(View view, MotionEvent e) {
//			          switch(e.getAction())
//			          {
//			             case MotionEvent.ACTION_DOWN:
//			            	 
//			            	 if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
//			            		LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, 
//			            										dislike, apk_repo_str_raw , 
//			            										apk_id, apk_ver_str_raw, EnumUserTaste.LIKE, userTaste);
//								loginComments.setOnDismissListener(ApkInfo.this);
//								loginComments.show();
//							 }else{
//								
//								 new AddTaste(
//						 				ApkInfo.this, 
//						 				apk_repo_str_raw,
//						 				apk_id, 
//						 				apk_ver_str_raw, 
//						 				sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//						 				sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//						 				EnumUserTaste.LIKE, likes, dislikes, like, dislike, userTaste).submit();
//								
//							 } 
//			            	 break;
//			          }
//			          return false;  //means that the listener dosen't consume the event
//			      }
//			});
//			this.dislike.setOnTouchListener(new OnTouchListener(){
//			      public boolean onTouch(View view, MotionEvent e) {
//			          switch(e.getAction())
//			          {
//			             case MotionEvent.ACTION_DOWN:
//			            	 
//			            	  if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
//			            		  	LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, dislike, 
//			            		  									apk_repo_str_raw, apk_id, apk_ver_str_raw, EnumUserTaste.DONTLIKE, userTaste);
//			            		  	loginComments.setOnDismissListener(ApkInfo.this);
//									loginComments.show();
//			            	  }else{
//			            		  
//			            		  new AddTaste(
//								 		ApkInfo.this, 
//								 		apk_repo_str_raw,
//								 		apk_id, 
//								 		apk_ver_str_raw, 
//								 		sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//								 		sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//								 		EnumUserTaste.DONTLIKE, likes, dislikes, like, dislike, userTaste).submit();
//								 
//			            	  }
//			                  break;
//			          }
//			          return false;  //means that the listener dosen't consume the event
//			      }
//			});
//			}else{
//				this.dislike.getLayoutParams().height=0;
//				this.like.getLayoutParams().height=0;
//			}
//		} else {
//			this.dislike.getLayoutParams().height=0;
//			this.like.getLayoutParams().height=0;
//			this.likes.getLayoutParams().height=0;
//			this.dislikes.getLayoutParams().height=0;
//		}
		
		
		

	}
	
	/**
	 * 
	 */
//	public void selectComments(){
//		 SharedPreferences sharedPreferences = ApkInfo.this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//		 if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null 
//				 && sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)!=null 
//				 && !taste.equals(EnumUserTaste.NOTEVALUATED) 
//				&& !taste.equals(EnumUserTaste.TASTELESS)){
//				
//			 new AddTaste(
//				 		ApkInfo.this, 
//				 		apk_repo_str_raw,
//				 		apk_id, 
//				 		apk_ver_str_raw, 
//				 		sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//				 		sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//				 		taste, likes, dislikes, like, dislike, userTaste).submit();
//			 
//			 taste = EnumUserTaste.TASTELESS;
//			 
//		 }
//	}
	
	/**
	 * 
	 * @param repo
	 * @param apkid
	 * @param version
	 * @param likes
	 * @param dontlikes
	 * @param like
	 * @param dislike
	 * @param userTaste
	 * @return
	 */
//	public void selectTaste(String repo, String apkid, String version, 
//							TextView likes, TextView dontlikes, ImageView like, 
//							ImageView dislike, WrapperUserTaste userTaste){
//		
//		likes.setText(this.getString(R.string.loading_likes));
//		dislikes.setText("");
////		dislike.setImageResource(R.drawable.dontlike);
////		like.setImageResource(R.drawable.like);
//		
//		if(tastePoster!=null)
//			tastePoster.cancel(true);
//		
//		tastePoster = new TastePoster(this, apkid, version, repo, likes, dontlikes, 
//													like, dislike, sharedPreferences.getString( Configs.LOGIN_USER_ID , null),
//													userTaste);
//		tastePoster.execute();
//		
//	}
//	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
//		//Log.d("Aptoide get",((AdapterContextMenuInfo)menuInfo).position+"");
//		if (((AdapterView.AdapterContextMenuInfo)menuInfo).id!=-1){
//			menu.setHeaderTitle(this.getString(R.string.whattodo));
//				for(Event item:Event.values())
//					menu.add(0, item.getId(), 0, item.getString());
//		}else{
//			super.onCreateContextMenu(menu, view, menuInfo);
//		}
//		
//	}
	
	/**
	 * 
	 * @param item
	 * @return If we handled the event or not. True in the first case.
	 */
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		Event event = Event.getEventFromId(item.getItemId());
//		Comment getted = commentAdapter.getItem((((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position-headers));
//		if(event!=null){
//			switch (event) {
//	        	case REPLY: 
//	        		//Open reply comment
//	        		Dialog commentDialog = new AddCommentDialog(ApkInfo.this, loadOnScrollCommentList, getted, like, dislike, 
//	        				apk_repo_str_raw,
//	        				apk_id, 
//					 		apk_ver_str_raw,
//					 		userTaste);
//					commentDialog.show();
//	        		return true;
//	        	case COPY_TO_CLIPBOARD:
//	        		ClipboardManager clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
//	        		clipManager.setText(getted.toString());
//	        		return true;
//	        	default : break;
//	        }
//		}
//		
//		return false;
//	}
	
	/**
	 * 
	 */
//	public void onDismiss(DialogInterface dialog) {
//		if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null && sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)!=null){
//			new AddTaste(
//	 				ApkInfo.this, 
//	 				apk_repo_str_raw,
//	 				apk_id, 
//	 				apk_ver_str_raw, 
//	 				sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//	 				sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//	 				((LoginDialog)dialog).getUserTaste(), likes, dislikes, like, dislike, userTaste).submit();
//		}
//	}
	
//	public void screenshotClick(View v){
//		//Log.d("Aptoide","This view.....");
//		final Dialog dialog = new Dialog(mctx);
//
//		dialog.setContentView(R.layout.screenshoot);
//		dialog.setTitle(apk_name_str);
//
//		ImageView image = (ImageView) dialog.findViewById(R.id.image);
//		ImageView fetch = (ImageView) v;
//		image.setImageDrawable(fetch.getDrawable());
//		image.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//		
//		dialog.setCanceledOnTouchOutside(true);
//		
//		dialog.show();
//		
//	}
//	
//	private Handler resetScreenshots;
//	private class ResetScreenshots extends Handler{
//		
//		private LinearLayout header;
//		
//		public ResetScreenshots(LinearLayout header) { this.header= header; }
//		
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			galleryView.setVisibility(View.GONE);
//			noscreens.setVisibility(View.GONE);
//			((ProgressBar) header.findViewById(R.id.pscreens)).setVisibility(View.VISIBLE);	
//		}
//	}
//	
//	private Handler updateScreenshots;
//	private class ScreenShotsUpdate extends Handler{
//		
//		private LinearLayout header;
//		
//		public ScreenShotsUpdate(LinearLayout header) { this.header= header; }
//		
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			ProgressBar pd = (ProgressBar) header.findViewById(R.id.pscreens);
//			pd.setVisibility(View.GONE);
//			int i = 0;
//			if(imageDrwb != null){
//				noscreens.setVisibility(View.GONE);
//				
//				for (Drawable pic : imageDrwb) {
//					screens.add(new ImageView(ApkInfo.this));
//					screens.getLast().setImageDrawable(pic);
//					i++;
//					if(i>=5)
//						break;
//				}
//				galleryView.setAdapter(new ImageAdapter(ApkInfo.this, imageDrwb, apk_name_str));
//				galleryView.setOnItemClickListener(new OnItemClickListener() {
//			        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//			        	
//			        	//Log.d("Aptoide","This view.....");
//			    		final Dialog dialog = new Dialog(mctx);
//
//			    		dialog.setContentView(R.layout.screenshoot);
//			    		dialog.setTitle(apk_name_str);
//
//			    		ImageView image = (ImageView) dialog.findViewById(R.id.image);
//			    		ImageView fetch = (ImageView) v;
//			    		image.setImageDrawable(fetch.getDrawable());
//			    		image.setOnClickListener(new OnClickListener() {
//			    			public void onClick(View v) {
//			    				dialog.dismiss();
//			    			}
//			    		});
//			    		
//			    		dialog.setCanceledOnTouchOutside(true);
//			    		
//			    		dialog.show();
//			    		
//			        }
//			    });
//				
//
//				galleryView.setVisibility(View.VISIBLE);
//			}else{
//				noscreens.setVisibility(View.VISIBLE);
//				noscreens.setText("No screenshots available.");
//			}
//			
//		}	
//		
//	}
	
	@Override
	public void finish() {
		if(jback)
			this.setResult(RESULT_OK, rtrn_intent);
		super.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
