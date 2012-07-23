package cm.aptoide.pt;

import cm.aptoide.pt.AvailableCursorAdapter.ViewHolder;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CategoryCursorAdapter extends CursorAdapter {
	DBHandler db;
	GetCategoryCount counter;
	Context context;
	private boolean secondaryCatg;
	public CategoryCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context=context;
		db = new DBHandler(context);
		counter = new GetCategoryCount(context);
	}

	@Override
	public void bindView(final View v, Context arg1, Cursor c) {
		ViewHolder holder = (ViewHolder) v.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name=((TextView) v.findViewById(R.id.text));
            holder.count = (TextView) v.findViewById(R.id.cat_count);
            v.setTag(holder);
        }
		
//		final Activity a = (Activity) arg1;
//		new Thread(new Runnable() {
//			public void run() {
//				final int i;
//				i = db.getCategoryCount(name,secondaryCatg);
//				a.runOnUiThread(new Runnable() {
//					public void run() {
//						((TextView) v.findViewById(R.id.cat_count))
//								.setText(i + " " + name);
//					}
//				});
//			}
//		}).start();
		counter.secondaryCategory=secondaryCatg;
		counter.getCount(c.getString(0), holder.count, context);
		holder.name.setText(c.getString(0));
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup viewgroup) {
		return LayoutInflater.from(context).inflate(R.layout.catglist, null);
	}
	
	public void setSecondaryCatg(boolean secondaryCatg) {
		this.secondaryCatg = secondaryCatg;
	}
	
	class ViewHolder{
		TextView name;
		TextView count;
	}

}
