package cm.aptoide.pt;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cm.aptoide.pt.comments.AddCommentDialog;
import cm.aptoide.pt.comments.Comment;
import cm.aptoide.pt.comments.CommentPosterListOnScrollListener;
import cm.aptoide.pt.comments.CommentsAdapter;
import cm.aptoide.pt.credentials.LoginDialog;
import cm.aptoide.pt.multiversion.MultiversionSpinnerAdapter;
import cm.aptoide.pt.multiversion.VersionApk;
import cm.aptoide.pt.taste.AddTaste;
import cm.aptoide.pt.taste.EnumUserTaste;
import cm.aptoide.pt.taste.TastePoster;
import cm.aptoide.pt.utils.ImageAdapter;



import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.content.DialogInterface.OnDismissListener;

public class ApkInfo extends Activity implements OnDismissListener{

	private final static String WS_img = "http://www.bazaarandroid.com/webservices/listApkScreens/";
	
	private SharedPreferences sharedPreferences;
	
	
	private Intent apkinfo = null;
	private Context mctx = null;
	
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
	private CommentsAdapter<Comment> commentAdapter;
	private CommentPosterListOnScrollListener loadOnScrollCommentList;
	private String apk_repo_str;
	private String apk_ver_str;
	private String apk_id;
	
	private ImageView like;
	private ImageView dislike;
	private TextView likes;
	private TextView dislikes;
	private String apk_repo_str_raw;
	private String apk_ver_str_raw;
	private String apk_size_str_raw;
	private EnumUserTaste taste;
	private WrapperUserTaste userTaste;
	private TastePoster tastePoster;
	
	private static final int HEADERS = 2; // The number of header items on the list view
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class WrapperUserTaste{
		
		private EnumUserTaste userTaste;
		private int operatingThreads;
		public WrapperUserTaste(){userTaste=EnumUserTaste.NOTEVALUATED; operatingThreads=0;}
		public EnumUserTaste getValue(){ return userTaste; } 
		public void setValue(EnumUserTaste userTaste){ this.userTaste = userTaste; }
		public void incOperatingThreads(){ operatingThreads++; }
		public void decOperatingThreads(){ 
			if(operatingThreads!=0)
				operatingThreads--;
		}
		public int getOperatingThreads(){ return operatingThreads; }
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apkinfo);
		
		sharedPreferences = ApkInfo.this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		
		Event.REPLY.setString(this.getString(R.string.reply));
		Event.COPY_TO_CLIPBOARD.setString(this.getString(R.string.copyclip));
		ListView listView = (ListView) findViewById(R.id.listComments);
		LayoutInflater inflater = this.getLayoutInflater();
		final LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.headercomments,listView, false);
		updateScreenshots = new ScreenShotsUpdate(linearLayout);
		
		this.likes = (TextView)linearLayout.findViewById(R.id.likes);
		this.dislikes = (TextView)linearLayout.findViewById(R.id.dislikes);
		
		this.like = ((ImageView)linearLayout.findViewById(R.id.likesImage));
		this.dislike = ((ImageView)linearLayout.findViewById(R.id.dislikesImage));
		
		this.userTaste = new WrapperUserTaste();
		this.taste = EnumUserTaste.NOTEVALUATED;
		
		tastePoster = null;
		
		mctx = this;
		screens = new LinkedList<ImageView>();

		galleryView = (Gallery) linearLayout.findViewById(R.id.galleryScreens);
		
		noscreens = (TextView) linearLayout.findViewById(R.id.noscreens);
		
		rtrn_intent = new Intent();
		
		apkinfo = getIntent();
		final boolean applicationExistsInRepo = apkinfo.getBooleanExtra("applicationExistsInRepo", true);
		
		final int versioncode = apkinfo.getIntExtra("vercode", 0);
		apk_id = apkinfo.getStringExtra("apk_id");
		final int type = apkinfo.getIntExtra("type", 0);
		
		String icon_path = apkinfo.getStringExtra("icon");
		apk_name_str = apkinfo.getStringExtra("name");
		String apk_descr = apkinfo.getStringExtra("about");
		apk_repo_str = apkinfo.getStringExtra("server");
		apk_ver_str = apkinfo.getStringExtra("version");
		String apk_dwon_str = apkinfo.getStringExtra("dwn");
		String apk_rat_str = apkinfo.getStringExtra("rat");
		String apk_size_str = apkinfo.getStringExtra("size");
		
		
		Button serch_mrkt = (Button)findViewById(R.id.btn_market);
		serch_mrkt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+apk_id));
				try{
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					Toast.makeText(mctx, getText(R.string.error_no_market), Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		final Button action = (Button) findViewById(R.id.btn1);
		switch (type) {
		case 0:
			action.setText(getString(R.string.install));
			break;

		case 1:
			action.setText(getString(R.string.rem));
			break;
			
		case 2:
			action.setText(getString(R.string.update));
			break;
		}
		
		spinnerMulti = ((Spinner)linearLayout.findViewById(R.id.spinnerMultiVersion));
		
		action.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int pos = -1;
				if(apkinfo.hasExtra("position")){
					pos = apkinfo.getIntExtra("position", -1);
				}
				switch (type) {
				case 0:
					
					rtrn_intent.putExtra("apkid", apk_id);
					rtrn_intent.putExtra("in", true);
					rtrn_intent.putExtra("position", pos);
					rtrn_intent.putExtra("version", ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
					jback = true;
					break;

				case 1:
					rtrn_intent.putExtra("apkid", apk_id);
					rtrn_intent.putExtra("rm", true);
					rtrn_intent.putExtra("position", pos);
					jback = true;
					break;


				case 2:
					rtrn_intent.putExtra("apkid", apk_id);
					rtrn_intent.putExtra("version", ((VersionApk)spinnerMulti.getSelectedItem()).getVersion());
					jback = true;
					break;
				}
				finish();
			}
		});
		
		ImageView icon = (ImageView) findViewById(R.id.appicon);
		File test_icon = new File(icon_path);
		
		
		if(test_icon.exists() && test_icon.length() > 0){
			icon.setImageDrawable(new BitmapDrawable(icon_path));
		}else{
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
		
		TextView apk_name = (TextView)findViewById(R.id.app_name);
		apk_name.setText(apk_name_str);
		
		TextView apk_about = (TextView)linearLayout.findViewById(R.id.descript);
		String desc_parsed = Html.fromHtml(apk_descr).toString();
		apk_about.setText(desc_parsed);
		
		final TextView apk_repo = (TextView)findViewById(R.id.app_repo);
		apk_repo.setText(apk_repo_str);
		
		TextView apk_version = (TextView)findViewById(R.id.app_ver);
		
		
		final VersionApk versionInstApk = (VersionApk) apkinfo.getParcelableExtra("instversion");
		
		if(type == 1){ 
			apk_version.setText(this.getString(R.string.version_inst)+": " + versionInstApk.getVersion());
		}else{
			apk_version.setVisibility(View.INVISIBLE);
		}
		
		
		TextView apk_down_n = (TextView)linearLayout.findViewById(R.id.dwn);
		apk_down_n.setText("Downloads: " + apk_dwon_str.replaceAll("\\n", "").replaceAll("\\t", "").trim());
		
		RatingBar apk_rat_n = (RatingBar) findViewById(R.id.rating);
		apk_rat_n.setRating(new Float(apk_rat_str));
		
		TextView apk_size_n = (TextView) linearLayout.findViewById(R.id.size);
		apk_size_n.setText(apk_size_str);

		new Thread(){
			public void run(){
				try{
					String ws_repo = apk_repo_str.substring(7).split("[\\.]")[0];
					String fetch_imgs = WS_img+ws_repo+"/"+apk_id+"/"+apk_ver_str.trim()+"/json";

					Log.d("Aptoide",apk_repo_str + " vs " + ws_repo);
					Log.d("Aptoide","Get img from: " + fetch_imgs);
					
					HttpResponse response_ws = NetworkApis.imgWsGet(fetch_imgs);
					if(response_ws != null && response_ws.getStatusLine().getStatusCode() == 200){
						String json_str = null;
						json_str = EntityUtils.toString(response_ws.getEntity());
						response_ws.getEntity().consumeContent();
						Log.d("Aptoide","Resp: " + json_str);
						JSONObject json_resp = new JSONObject(json_str);
						
						JSONArray img_url = json_resp.getJSONArray("listing");
						if(img_url.length()>0)
							imageDrwb = new Drawable[img_url.length()];
						for(int i = 0; i< img_url.length(); i++){
							String a = (String)img_url.get(i);
							Log.d("Aptoide","* " + a);
							HttpResponse pic = NetworkApis.imgWsGet(a);
							InputStream pic_st = pic.getEntity().getContent();
							//java.lang.OutOfMemoryError: bitmap size exceeds VM budget
							Drawable pic_drw = Drawable.createFromStream(pic_st, "src"); //hear
							imageDrwb[i] = pic_drw;
						}
					}
					
				}catch (Exception e ){ }
				finally{
					updateScreenshots.sendEmptyMessage(0);
				}
			}
		}.start();
		
		//TODO This as to be changed in a future code revision
		apk_size_str_raw 	= apk_size_str.substring(6);
		
		if(apk_size_str_raw.equals("No information available")){ apk_size_str_raw = "0";}
		else { apk_size_str_raw = apk_size_str_raw.substring(0,apk_size_str_raw.length()-2); }
		;
		if(type == 1) { apk_ver_str_raw = versionInstApk.getVersion(); } 
		else { apk_ver_str_raw = apk_ver_str.substring(1,apk_ver_str.length()-1); }
		
		apk_repo_str_raw 	= apk_repo_str.substring("http://".length(),apk_repo_str.indexOf(".bazaarandroid.com"));
		
		if(!applicationExistsInRepo){
			//Hide taste section
			this.like.setVisibility(View.GONE);
			this.dislike.setVisibility(View.GONE);
			this.likes.setVisibility(View.GONE);
			this.dislikes.setVisibility(View.GONE);
		}
		
		listView.addHeaderView(linearLayout, null, false);
		commentAdapter = new CommentsAdapter<Comment>(this, R.layout.commentlistviewitem, new ArrayList<Comment>());
		
		
		/*Comments*/
		if(applicationExistsInRepo){
			//Stop comments for applications not present in the repository
			
			listView.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(position==1){ // If comment app... option selected
						Dialog commentDialog = new AddCommentDialog(ApkInfo.this, loadOnScrollCommentList, null, like, dislike, 
								apk_repo_str_raw,
				 				apk_id, 
				 				apk_ver_str_raw,
				 				userTaste);
						commentDialog.show();
					}
				}
			});
			//listView.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.apkinfoheader));
			TextView textView = new TextView(this);
			textView.setText(this.getString(R.string.commentlabel));
			textView.setTextSize(20);
			textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			textView.setPadding(0, 10, 0, 10);
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			listView.addHeaderView(textView);
			
			LinearLayout loadComLayout = (LinearLayout) inflater.inflate(R.layout.loadingfootercomments,listView, false);
			listView.addFooterView(loadComLayout);
			listView.setAdapter(commentAdapter);
			try {
				loadOnScrollCommentList = new CommentPosterListOnScrollListener(this, commentAdapter, apk_repo_str_raw, apk_id, apk_ver_str_raw, loadComLayout);
				listView.setOnScrollListener(loadOnScrollCommentList);
			} 
			//catch (ParserConfigurationException e) 	{} 
			//catch (SAXException e) 					{}
			catch(Exception e)							{}
			
			registerForContextMenu(listView);
		} else {
			listView.setAdapter(commentAdapter);
		}
		
		
		
		
		
		
		/*Multiversion*/
		if(type!=1){//If we aren't in the installed tab
		
			ArrayList<VersionApk> versions = apkinfo.getParcelableArrayListExtra("oldVersions");
			versions.add(
					new VersionApk(apk_ver_str_raw, 
					versioncode, 
					apk_id, 
					Integer.parseInt(apk_size_str_raw)
					)
			);
			Collections.sort(versions, Collections.reverseOrder());
			
			final MultiversionSpinnerAdapter<VersionApk> spinnerMultiAdapter 
				= new MultiversionSpinnerAdapter<VersionApk>(this, R.layout.textviewfocused, versions, "Version", "Size");
			spinnerMultiAdapter.setDropDownViewResource(R.layout.multiversionspinneritem);
			spinnerMulti.setAdapter(spinnerMultiAdapter );
			if(type==2){
				//If we are in tab updates
				spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						
							VersionApk versionApk = ((VersionApk)spinnerMultiAdapter.getItem(position));
							apk_ver_str_raw = versionApk.getVersion();
							selectTaste(apk_repo_str_raw , apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
							int result = versionApk.compareTo(versionInstApk);
							if(result>0){
								action.setText("Update");
							}else if(result<0) {
								action.setText("Downgrade");
							}else{
								action.setText(ApkInfo.this.getString(R.string.isinstalled));
							}
							
							loadOnScrollCommentList.fetchNewApp(apk_repo_str_raw, apk_id, apk_ver_str_raw);
							
					}
					public void onNothingSelected(AdapterView<?> parent) {}
				});
			} else if(type==0){
				
				//If we are in tab available
				spinnerMulti.setOnItemSelectedListener(new OnItemSelectedListener(){
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						apk_ver_str_raw = ((VersionApk)spinnerMultiAdapter.getItem(position)).getVersion();
						selectTaste(apk_repo_str_raw , apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
						
						loadOnScrollCommentList.fetchNewApp(apk_repo_str_raw, apk_id, apk_ver_str_raw);
						
					}
					public void onNothingSelected(AdapterView<?> parent) {}
				});
			}
			//Select the current version installed by the user
		}else{
			//Otherwise
			spinnerMulti.getLayoutParams().height = 0;
			spinnerMulti.getLayoutParams().width = 0;
			spinnerMulti.setVisibility(View.INVISIBLE);
		}
		
		
		
		
		
		/*Taste*/
		if(applicationExistsInRepo){
			
			if(type==1)
				selectTaste(apk_repo_str_raw, apk_id, apk_ver_str_raw, likes, dislikes, like, dislike, userTaste);
			
			this.like.setOnTouchListener(new OnTouchListener(){
			      public boolean onTouch(View view, MotionEvent e) {
			          switch(e.getAction())
			          {
			             case MotionEvent.ACTION_DOWN:
			            	 
			            	 if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
			            		LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, 
			            										dislike, apk_repo_str_raw , 
			            										apk_id, apk_ver_str_raw, EnumUserTaste.LIKE, userTaste);
								loginComments.setOnDismissListener(ApkInfo.this);
								loginComments.show();
							 }else{
								 
								 boolean userTasteBufEquals = false;
								 synchronized(userTaste){
									 userTasteBufEquals = userTaste.getValue().equals(EnumUserTaste.LIKE);	 
								 }
								 
								 if(!userTasteBufEquals){
								 
									 new AddTaste(
							 				ApkInfo.this, 
							 				apk_repo_str_raw ,
							 				apk_id, 
							 				apk_ver_str_raw, 
							 				sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
							 				sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
							 				EnumUserTaste.LIKE, likes, dislikes, like, dislike, userTaste, null).submit();
									 
								 } else {
									 
									 Toast.makeText(ApkInfo.this, ApkInfo.this.getString(R.string.opinionsuccess), Toast.LENGTH_LONG).show();
									 
								 }
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
			            	 
			            	  if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
			            		  	LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, dislike, 
			            		  									apk_repo_str_raw, apk_id, apk_ver_str_raw, EnumUserTaste.DONTLIKE, userTaste);
			            		  	loginComments.setOnDismissListener(ApkInfo.this);
									loginComments.show();
			            	  }else{
			            		  
			            		 boolean userTasteBufEquals = false;
								 synchronized(userTaste){
									 userTasteBufEquals = userTaste.getValue().equals(EnumUserTaste.DONTLIKE);	 
								 }
								 
								 if(!userTasteBufEquals){
			            		  new AddTaste(
								 		ApkInfo.this, 
								 		apk_repo_str_raw,
								 		apk_id, 
								 		apk_ver_str_raw, 
								 		sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
								 		sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
								 		EnumUserTaste.DONTLIKE, likes, dislikes, like, dislike, userTaste, null).submit();
								 } else {
									 Toast.makeText(ApkInfo.this, ApkInfo.this.getString(R.string.opinionsuccess), Toast.LENGTH_LONG).show();
								 }
								 
			            	  }
			                  break;
			          }
			          return false;  //means that the listener dosen't consume the event
			      }
			});
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 */
	public void selectComments(){
		 SharedPreferences sharedPreferences = ApkInfo.this.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		 if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null 
				 && sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)!=null 
				 && !taste.equals(EnumUserTaste.NOTEVALUATED) 
				&& !taste.equals(EnumUserTaste.TASTELESS)){
				
			 new AddTaste(
				 		ApkInfo.this, 
				 		apk_repo_str_raw,
				 		apk_id, 
				 		apk_ver_str_raw, 
				 		sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
				 		sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
				 		taste, likes, dislikes, like, dislike, userTaste, null).submit();
			 
			 taste = EnumUserTaste.TASTELESS;
			 
		 }
	}
	
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
	public void selectTaste(String repo, String apkid, String version, 
							TextView likes, TextView dontlikes, ImageView like, 
							ImageView dislike, WrapperUserTaste userTaste){
		
		likes.setText(this.getString(R.string.loading));
		dontlikes.setText(this.getString(R.string.loading));
		
		if(tastePoster!=null)
			tastePoster.cancel(true);
		
		tastePoster = new TastePoster(this, apkid, version, repo, likes, dontlikes, 
													like, dislike, sharedPreferences.getString( Configs.LOGIN_USER_ID , null),
													userTaste);
		tastePoster.execute();
		
	}

	/**
	 * 
	 * @author rafael
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
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		//Log.d("Aptoide get",((AdapterContextMenuInfo)menuInfo).position+"");
		if (((AdapterView.AdapterContextMenuInfo)menuInfo).id!=-1){
			menu.setHeaderTitle(this.getString(R.string.whattodo));
				for(Event item:Event.values())
					menu.add(0, item.getId(), 0, item.getString());
		}else{
			super.onCreateContextMenu(menu, view, menuInfo);
		}
		
	}
	
	/**
	 * 
	 * @param item
	 * @return If we handled the event or not. True in the first case.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Event event = Event.getEventFromId(item.getItemId());
		Comment getted = commentAdapter.getItem((((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position-HEADERS));
		if(event!=null){
			switch (event) {
	        	case REPLY: 
	        		//Open reply comment
	        		Dialog commentDialog = new AddCommentDialog(ApkInfo.this, loadOnScrollCommentList, getted, like, dislike, 
	        				apk_repo_str_raw,
	        				apk_id, 
					 		apk_ver_str_raw,
					 		userTaste);
					commentDialog.show();
	        		return true;
	        	case COPY_TO_CLIPBOARD:
	        		ClipboardManager clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
	        		clipManager.setText(getted.toString());
	        		return true;
	        	default : break;
	        }
		}
		
		return false;
	}
	
	/**
	 * 
	 */
	public void onDismiss(DialogInterface dialog) {
		if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null && sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)!=null){
			new AddTaste(
	 				ApkInfo.this, 
	 				apk_repo_str_raw,
	 				apk_id, 
	 				apk_ver_str_raw, 
	 				sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
	 				sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
	 				((LoginDialog)dialog).getUserTaste(), likes, dislikes, like, dislike, userTaste, ApkInfo.this).submit();
		}
	}
	
	
	
	
		
	public void screenshotClick(View v){
		//Log.d("Aptoide","This view.....");
		final Dialog dialog = new Dialog(mctx);

		dialog.setContentView(R.layout.screenshoot);
		dialog.setTitle(apk_name_str);

		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		ImageView fetch = (ImageView) v;
		image.setImageDrawable(fetch.getDrawable());
		image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setCanceledOnTouchOutside(true);
		
		dialog.show();
		
	}
	
	private Handler updateScreenshots;
	
	private class ScreenShotsUpdate extends Handler{
		
		private LinearLayout header;
		
		public ScreenShotsUpdate(LinearLayout header) { this.header= header; }
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressBar pd = (ProgressBar) header.findViewById(R.id.pscreens);
			pd.setVisibility(View.GONE);
			int i = 0;
			if(imageDrwb != null){
				noscreens.setVisibility(View.GONE);
				
				for (Drawable pic : imageDrwb) {
					screens.add(new ImageView(ApkInfo.this));
					screens.getLast().setImageDrawable(pic);
					i++;
					if(i>=5)
						break;
				}
				galleryView.setAdapter(new ImageAdapter(ApkInfo.this, imageDrwb, apk_name_str));
				galleryView.setOnItemClickListener(new OnItemClickListener() {
			        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			        	
			        	//Log.d("Aptoide","This view.....");
			    		final Dialog dialog = new Dialog(mctx);

			    		dialog.setContentView(R.layout.screenshoot);
			    		dialog.setTitle(apk_name_str);

			    		ImageView image = (ImageView) dialog.findViewById(R.id.image);
			    		ImageView fetch = (ImageView) v;
			    		image.setImageDrawable(fetch.getDrawable());
			    		image.setOnClickListener(new OnClickListener() {
			    			public void onClick(View v) {
			    				dialog.dismiss();
			    			}
			    		});
			    		
			    		dialog.setCanceledOnTouchOutside(true);
			    		
			    		dialog.show();
			    		
			        }
			    });
				

				
			}else{
				noscreens.setVisibility(View.VISIBLE);
				noscreens.setText("No screenshots available.");
			}
			
		}	
		
	}
	
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
