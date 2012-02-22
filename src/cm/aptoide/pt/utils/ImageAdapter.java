package cm.aptoide.pt.utils;

import java.util.LinkedList;

import cm.aptoide.pt.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class ImageAdapter extends BaseAdapter {
	
    private Context context;
    private int itemBackground;
    private LinkedList<Bitmap> drawables;
    private String apk_name_str;
    
    /**
     * 
     * @param c
     * @param imageDrwb
     * @param apk_name_str
     */
    public ImageAdapter(Context c, LinkedList<Bitmap> imageDrwb, String apk_name_str) {
        context = c;
        this.drawables = imageDrwb;
        this.apk_name_str = apk_name_str;
        //Setting the style
        TypedArray a = context.obtainStyledAttributes(R.styleable.galleryScreens);
        itemBackground = a.getResourceId(
            R.styleable.galleryScreens_android_galleryItemBackground, 0);
        a.recycle();                    
    }
    
    /**
     * Returns the number of images
     */
    public int getCount() {
        return drawables.size();
    }
    
    /**
     * Returns the ID of an item
     */
    public Object getItem(int position) {
        return position;
    }            

    public long getItemId(int position) {
        return position;
    }
    
    /**
     * Returns an ImageView view.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(drawables.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_END);
        if(drawables.get(position)!=null){
	        imageView.setLayoutParams(
	        		new Gallery.LayoutParams(
	        				(int)(drawables.get(position).getWidth()*0.5), 
	        				(int)(drawables.get(position).getHeight()*0.5)
	        				)
	        		);
	        imageView.setBackgroundResource(itemBackground);
        }else{
        	Log.e("Aptoide","Screenshots Error");
        }
        
        
        return imageView;
    }
    
    /**
     * 
     * @param parent
     * @param v
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {                
        
    	final Dialog dialog = new Dialog(context);

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
    
}