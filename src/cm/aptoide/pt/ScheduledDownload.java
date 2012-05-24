package cm.aptoide.pt;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduledDownload extends FragmentActivity implements LoaderCallbacks<Cursor>{
	ListView lv;
	Context context;
	DBHandler db;
	private CursorAdapter adapter;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		db = new DBHandler(context);
		db.open();
		setContentView(R.layout.sch_downloadempty);
		lv = (ListView) findViewById(android.R.id.list);
		adapter =new CursorAdapter(context,null,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
			ImageLoader imageLoader = new ImageLoader(context);
			@Override
			public View newView(Context context, Cursor arg1, ViewGroup v) {
				return LayoutInflater.from(context).inflate(R.layout.sch_download, null);
			}
			
			@Override
			public void bindView(View v, Context arg1, Cursor c) {
				((TextView) v.findViewById(R.id.isinst)).setText(c.getString(3));
				((TextView) v.findViewById(R.id.name)).setText(c.getString(2));
				imageLoader.DisplayImage(-1, c.getString(5), (ImageView) v.findViewById(R.id.appicon), arg1);
				
			}
		};
		lv.setAdapter(adapter);
		redraw();
	}
	private void redraw() {
		getSupportLoaderManager().restartLoader(0x50, null, this);
	}
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader a = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				
				return db.getScheduledDownloads();
			}
		};
		return a;
	}
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		adapter.swapCursor(c);
		if(c.getCount()==0){
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}else{
			findViewById(android.R.id.empty).setVisibility(View.GONE);
		}
	}
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	
	
}
