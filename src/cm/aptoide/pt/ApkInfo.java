package cm.aptoide.pt;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ApkInfo extends Activity{

	private Intent apkinfo = null;
	private Context mctx = null;
	
	private Intent rtrn_intent = null;
	
	private boolean jback = false;
	
	private Integer[] imageIDs = {
			R.drawable.no_screen
	};
	
	//private Drawable[] imageIDs = new Drawable[1];
	
	private String apk_name_str = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apkinfo);
		
		mctx = this;
		
		rtrn_intent = new Intent();
		
		apkinfo = getIntent();
		
		final String apk_id = apkinfo.getStringExtra("apk_id");
		final int type = apkinfo.getIntExtra("type", 0);
		
		String icon_path = apkinfo.getStringExtra("icon");
		apk_name_str = apkinfo.getStringExtra("name");
		String apk_descr = apkinfo.getStringExtra("about");
		String apk_repo_str = apkinfo.getStringExtra("server");
		String apk_ver_str = apkinfo.getStringExtra("version");
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
			//imageIDs[0] = new BitmapDrawable(icon_path);
		}else{
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
			//imageIDs[0] = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
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
		
		Gallery galry = (Gallery)findViewById(R.id.screenshots_gal);
		galry.setAdapter(new GalAdpt(this));
		galry.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Dialog dialog = new Dialog(mctx);

				dialog.setContentView(R.layout.screenshoot);
				dialog.setTitle(apk_name_str);

				ImageView image = (ImageView) dialog.findViewById(R.id.image);
				image.setImageResource(imageIDs[arg2]);
				//image.setImageDrawable(imageIDs[arg2]);
				dialog.show();
			}
		
		
		});
		
		
	}

	public class GalAdpt extends BaseAdapter{
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
            return imageIDs.length;
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
            imageView.setImageResource(imageIDs[position]);
            //imageView.setImageDrawable(imageIDs[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
	}
	
	
	@Override
	public void finish() {
		if(jback)
			this.setResult(RESULT_OK, rtrn_intent);
		super.finish();
	}

	 
	
	

}
