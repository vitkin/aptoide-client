package cm.aptoide.pt2.adapters;

import cm.aptoide.pt2.Database;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.R.id;
import cm.aptoide.pt2.R.layout;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class InstalledAdapter extends CursorAdapter {

	
	private ImageLoader loader;

	public InstalledAdapter(Context context, Cursor c, int flags,Database db) {
		super(context, c, flags);
		loader = new ImageLoader(context, db);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View v = LayoutInflater.from(context).inflate(R.layout.app_row, null);
		
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.app_name);
            holder.icon= (ImageView) view.findViewById(R.id.app_icon);
            holder.vername= (TextView) view.findViewById(R.id.installed_versionname);
            holder.downloads= (TextView) view.findViewById(R.id.downloads);
            holder.rating= (RatingBar) view.findViewById(R.id.stars);
            view.setTag(holder);
        }
		holder.name.setText(cursor.getString(1));
		loader.DisplayImage(cursor.getLong(3), cursor.getString(4), holder.icon, context,false,(cursor.getString(cursor.getColumnIndex("apkid"))+"|"+cursor.getString(cursor.getColumnIndex("vercode"))).hashCode()+"");
		 try{
	        	holder.rating.setRating(Float.parseFloat(cursor.getString(5)));	
	        }catch (Exception e) {
	        	holder.rating.setRating(0);
			}
		 holder.downloads.setText(cursor.getString(6));
		holder.vername.setText(cursor.getString(2));
	}
	
	private static class ViewHolder {
		TextView name;
		TextView vername;
		ImageView icon;
		RatingBar rating;
		TextView downloads;
	}

}
