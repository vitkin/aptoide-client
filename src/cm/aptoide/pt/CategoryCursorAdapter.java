package cm.aptoide.pt;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoryCursorAdapter extends CursorAdapter {
	DBHandler db;
	private boolean secondaryCatg;
	public CategoryCursorAdapter(Context context, Cursor c, int flags,final boolean secondaryCatg) {
		super(context, c, flags);
		db = new DBHandler(context);
		this.secondaryCatg = secondaryCatg;
	}

	@Override
	public void bindView(final View v, Context arg1, Cursor c) {
		final String name = c.getString(0);
		final Activity a = (Activity) arg1;
		new Thread(new Runnable() {
			public void run() {
				final int i;
				if(secondaryCatg){
					i = db.getCategoryCount(name,secondaryCatg);
				}else{
					i = db.getCategoryCount(name,secondaryCatg);
				}
				a.runOnUiThread(new Runnable() {
					public void run() {
						((TextView) v.findViewById(R.id.cat_count))
								.setText(i + " " + name);
					}
				});
			}
		}).start();
		((TextView) v.findViewById(R.id.text)).setText(name);
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup viewgroup) {
		return LayoutInflater.from(context).inflate(R.layout.catglist, null);
	}

}
