package cm.aptoide.pt;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ApkInfo extends Activity{

	private final static String WS_img = "http://www.bazaarandroid.com/webservices/listApkScreens/";
	
	private Intent apkinfo = null;
	private Context mctx = null;
	
	private Intent rtrn_intent = null;
	
	private boolean jback = false;
	
	private Integer[] imageIDs = {
			R.drawable.no_screen
	};
	
	private Drawable[] imageDrwb = null;
	
	//private Gallery galry = null;
	
	private String apk_name_str = null;
	
	/*ImageView sht1 = null;
	ImageView sht2 = null;
	ImageView sht3 = null;
	ImageView sht4 = null;
	ImageView sht5 = null;*/
	
	List<ImageView> screens = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apkinfo);
		
		mctx = this;
		screens = new ArrayList<ImageView>();
		//sht1 = (ImageView) findViewById(R.id.shot1);
		screens.add((ImageView) findViewById(R.id.shot1));
		screens.add((ImageView) findViewById(R.id.shot2));
		screens.add((ImageView) findViewById(R.id.shot3));
		screens.add((ImageView) findViewById(R.id.shot4));
		screens.add((ImageView) findViewById(R.id.shot5));
//		sht1 = (ImageView) findViewById(R.id.shot1);
//		screens.add(sht1);
//		sht1 = (ImageView) findViewById(R.id.shot1);
//		screens.add(sht1);
//		sht1 = (ImageView) findViewById(R.id.shot1);
//		screens.add(sht1);
//		sht1 = (ImageView) findViewById(R.id.shot1);
//		screens.add(sht1);
		
		rtrn_intent = new Intent();
		
		apkinfo = getIntent();
		
		final String apk_id = apkinfo.getStringExtra("apk_id");
		final int type = apkinfo.getIntExtra("type", 0);
		
		String icon_path = apkinfo.getStringExtra("icon");
		apk_name_str = apkinfo.getStringExtra("name");
		String apk_descr = apkinfo.getStringExtra("about");
		final String apk_repo_str = apkinfo.getStringExtra("server");
		final String apk_ver_str = apkinfo.getStringExtra("version");
		String apk_dwon_str = apkinfo.getStringExtra("dwn");
		String apk_rat_str = apkinfo.getStringExtra("rat");
		String apk_size_str = apkinfo.getStringExtra("size");
		
		/*try{
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
					Drawable pic_drw = Drawable.createFromStream(pic_st, "src");
					imageDrwb[i] = pic_drw;
				}
			}
		}catch (Exception e ){ }*/
		
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
		
		Button action = (Button) findViewById(R.id.btn1);
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
		
		TextView apk_about = (TextView)findViewById(R.id.descript);
		apk_about.setText(apk_descr);
		
		TextView apk_repo = (TextView)findViewById(R.id.app_repo);
		apk_repo.setText(apk_repo_str);
		
		TextView apk_version = (TextView)findViewById(R.id.app_ver);
		apk_version.setText("Version: " + apk_ver_str.replaceAll("\\n", ""));
		
		TextView apk_down_n = (TextView)findViewById(R.id.dwn);
		apk_down_n.setText("Downloads: " + apk_dwon_str.replaceAll("\\n", "").replaceAll("\\t", "").trim());
		
		RatingBar apk_rat_n = (RatingBar) findViewById(R.id.rating);
		apk_rat_n.setRating(new Float(apk_rat_str));
		
		TextView apk_size_n = (TextView) findViewById(R.id.size);
		apk_size_n.setText(apk_size_str);
		
		/*galry = (Gallery)findViewById(R.id.screenshots_gal);
		galry.setAdapter(new GalAdpt(this));
		galry.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Dialog dialog = new Dialog(mctx);

				dialog.setContentView(R.layout.screenshoot);
				dialog.setTitle(apk_name_str);

				ImageView image = (ImageView) dialog.findViewById(R.id.image);
				if(imageDrwb == null)
					image.setImageResource(imageIDs[arg2]);
				else
					image.setImageDrawable(imageDrwb[arg2]);
				dialog.show();
			}
		
		
		});*/
		
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
							Drawable pic_drw = Drawable.createFromStream(pic_st, "src");
							imageDrwb[i] = pic_drw;
						}
					}
					
				}catch (Exception e ){ }
				finally{
					updateScreenshots.sendEmptyMessage(0);
				}
			}
		}.start();
		
	}

	/*public class GalAdpt extends BaseAdapter{
		private Context context;
        private int itemBackground;
 
        public GalAdpt(Context c) 
        {
            context = c;
            //---setting the style---
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();                    
        }
 
        //---returns the number of images---
        public int getCount() {
        	if(imageDrwb == null)
        		return imageIDs.length;
        	else
        		return imageDrwb.length;
        }
 
        //---returns the ID of an item--- 
        public Object getItem(int position) {
            return position;
        }            
 
        public long getItemId(int position) {
            return position;
        }
 
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            if(imageDrwb == null)
            	imageView.setImageResource(imageIDs[position]);
            else
            	imageView.setImageDrawable(imageDrwb[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
	}*/
	
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
	
	private Handler updateScreenshots = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressBar pd = (ProgressBar) findViewById(R.id.pscreens);
			pd.setVisibility(View.GONE);
			int i = 0;
			if(imageDrwb != null){
				for (Drawable pic : imageDrwb) {
					screens.get(i).setImageDrawable(pic);
					i++;
					if(i>=5)
						break;
				}
			}
			//galry.setAdapter(new GalAdpt(mctx));
		}		
	};
	
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
