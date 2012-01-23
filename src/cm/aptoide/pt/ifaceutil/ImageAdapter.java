package cm.aptoide.pt.ifaceutil;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import cm.aptoide.pt.R;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class ImageAdapter extends BaseAdapter {
	
    private Context context;
    private int itemBackground;
    private ArrayList<Drawable>  drawables;
    private String appName;
    
    /**
     * 
     * @param context
     * @param drawables
     * @param appName
     */
    public ImageAdapter(Context context, ArrayList<Drawable> drawables, String appName) {
        this.context = context;
        this.drawables = drawables;
        this.appName = appName;
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
        imageView.setImageDrawable(drawables.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_END);
        if(drawables.get(position)!=null){
	        imageView.setLayoutParams(
	        		new Gallery.LayoutParams(
	        				(int)(drawables.get(position).getMinimumWidth()*0.5), 
	        				(int)(drawables.get(position).getMinimumHeight()*0.5)
	        				)
	        		);
	        imageView.setBackgroundResource(itemBackground);
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

		dialog.setContentView(R.layout.screenshot);
		dialog.setTitle(appName);

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