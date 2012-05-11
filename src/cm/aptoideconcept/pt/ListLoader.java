package cm.aptoideconcept.pt;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

public class ListLoader implements LoaderManager.LoaderCallbacks<Cursor> {
	
	CursorAdapter adapter;
	Context context;
	Cursor cursor;
	long startTime;
	long endTime;
	public ListLoader(Context context, CursorAdapter listView, Cursor cursor) {
		this.adapter=listView;
		this.context=context;
		this.cursor=cursor;
	}
	
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		startTime=System.currentTimeMillis();
		SimpleCursorLoader loader = new SimpleCursorLoader(context) {
			
			@Override
			public Cursor loadInBackground() {
				return cursor;
			}
		};
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
//		adapter.swapCursor(cursor);
		endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}

	

}
