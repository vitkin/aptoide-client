/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import cm.aptoide.pt.adapters.InstalledAdapter;
import cm.aptoide.pt.contentloaders.SimpleCursorLoader;
import cm.aptoide.pt.R;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchManager extends FragmentActivity implements LoaderCallbacks<Cursor>{
	ListView lv;
	String query;
//	EditText searchBox;
	Database db;
	View v;
	private InstalledAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		System.out.println("onCreate Search");
		db = Database.getInstance();
		setContentView(R.layout.searchmanager);
		if(getIntent().hasExtra("search")){
			query = getIntent().getExtras().getString("search");
		}else{
			query = getIntent().getExtras().getString(android.app.SearchManager.QUERY).replaceAll("\\s{2,}|\\W", " ").trim();
			query = query.replaceAll("\\s{2,}", " ");
		}
		lv = (ListView) findViewById(R.id.listView);
//		searchBox = (EditText) findViewById(R.id.search_box);
		v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_aptoide, null);
		lv.addFooterView(v);
		Button bazaar_search =  (Button) v.findViewById(R.id.baz_src);
		bazaar_search.setText(getString(R.string.search_log)+" '"+query+"' "+getString(R.string.search_stores));
		
		bazaar_search.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String url = "http://m.aptoide.com/searchview.php?search="+query;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		adapter = new InstalledAdapter(this,null,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,db);
		lv.setAdapter(adapter);
		
//		searchBox.setText(query);
//		searchBox.addTextChangedListener(new TextWatcher() {
//			
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				
//			}
//			
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				
//			}
//			
//			public void afterTextChanged(Editable editable) {
//				query=editable.toString().replaceAll("[\\%27]|[\\']|[\\-]{2}|[\\%23]|[#]|\\s{2,}", " ").trim();
//				if(editable.length()>2){
//					getSupportLoaderManager().restartLoader(0x30, null, SearchManager.this);
//					
//				}
//				((TextView) v.findViewById(R.id.baz_src)).setText(getString(R.string.search_log)+" '"+query+"' "+getString(R.string.search_stores));
//			}
//		});
		
		getSupportLoaderManager().restartLoader(0x30, null, this);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position,
					long arg3) {
				Intent i = new Intent(SearchManager.this,ApkInfo.class);
				i.putExtra("_id", arg3);
				i.putExtra("category", Category.INFOXML.ordinal());
				startActivity(i);
			}
		});
	}
	
	
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		SimpleCursorLoader a = new SimpleCursorLoader(this) {
			
			@Override
			public Cursor loadInBackground() {
				
				return db.getSearch(query);
			}
		};
		return a;
	}
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
		
	}
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);		
	}
}
