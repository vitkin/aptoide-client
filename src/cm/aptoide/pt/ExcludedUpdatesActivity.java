package cm.aptoide.pt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExcludedUpdatesActivity extends Activity {
	ArrayList<ExcludedUpdate> excludedUpdates = new ArrayList<ExcludedUpdate>();
	Database db = Database.getInstance();
	ListView lv;
	TextView tv_no_excluded_downloads;
	Button bt_restore_updates;
	ArrayAdapter<ExcludedUpdate> adapter;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.excluded_uploads_list);
		lv = (ListView) findViewById(R.id.excluded_updates_list);
		tv_no_excluded_downloads = (TextView) findViewById(R.id.tv_no_excluded_downloads);
		context = this;
		adapter = new ArrayAdapter<ExcludedUpdate>(this, 0, excludedUpdates) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v;
		        if (convertView == null) {
		            v = newView(parent);
		        } else {
		            v = convertView;
		        }
		        bindView(v, position);
				return v;
			}
			
			public View newView(ViewGroup arg2) {
				return LayoutInflater.from(context).inflate(R.layout.excluded_update, null);
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			public void bindView(View convertView, int c) {
				ExcludedUpdate excludedUpdate = getItem(c); 
				
				CheckBox cb_exclude; 
				TextView tv_name;
				TextView tv_vercode;
				TextView tv_apkid; 
				
				if ( convertView.getTag() == null ) {
					tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					tv_vercode = (TextView) convertView.findViewById(R.id.tv_vercode);
					tv_apkid = (TextView) convertView.findViewById(R.id.tv_apkid);
					cb_exclude = (CheckBox) convertView.findViewById(R.id.cb_exclude);
					convertView.setTag(new ExcludedUpdatesHolder(tv_name, tv_apkid, tv_vercode, cb_exclude));
					
					cb_exclude.setOnClickListener( new View.OnClickListener() {
						public void onClick(View v) {
							CheckBox cb = (CheckBox) v ;
							ExcludedUpdate excludedUpdateItem = (ExcludedUpdate) cb.getTag();
							excludedUpdateItem.setChecked(cb.isChecked());
						}
					});
				}else {
					ExcludedUpdatesHolder viewHolder = (ExcludedUpdatesHolder) convertView.getTag();
					cb_exclude = viewHolder.cb_exclude;
					tv_vercode = viewHolder.tv_vercode;
					tv_name = viewHolder.tv_name;
					tv_apkid = viewHolder.tv_apkid;
				}
				cb_exclude.setTag(excludedUpdate); 
				cb_exclude.setChecked(cb_exclude.isChecked());
				tv_name.setText(excludedUpdate.getName());  
				tv_vercode.setText(""+excludedUpdate.getVercode());
				tv_apkid.setText(excludedUpdate.getApkid());
			}
		};
		redraw();
		
		bt_restore_updates = (Button) findViewById(R.id.restore_update);
		bt_restore_updates.setText(getString(R.string.restore_updates));
		bt_restore_updates.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(isAllChecked()){
					for(ExcludedUpdate excludedUpdate : excludedUpdates){
						if(excludedUpdate.checked){
							db.deleteFromExcludeUpdate(excludedUpdate.apkid, excludedUpdate.vercode);
						}
					}
					redraw();
				} else {
					Toast toast= Toast.makeText(ExcludedUpdatesActivity.this, 
							R.string.no_excluded_updates_selected, Toast.LENGTH_SHORT);  
					toast.show();
				}
			}
		});
		lv.setAdapter(adapter);
		

	}

	private boolean isAllChecked(){
		if(adapter.isEmpty()){
			return false;
		}
		for(ExcludedUpdate excludedUpdate: excludedUpdates){
			if (excludedUpdate.checked){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	private void redraw() {
		Cursor c = db.getExcludedApks();
		excludedUpdates.clear();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			ExcludedUpdate excludedUpdate = new ExcludedUpdate(c.getString(1), c.getString(0), c.getInt(2));
			excludedUpdates.add(excludedUpdate);
		}
		adapter.notifyDataSetChanged();
		
		Log.d("ExcludedUpdatesActivity","excluded updates: " + excludedUpdates.toString());
		
		if(!adapter.isEmpty()){
			tv_no_excluded_downloads.setVisibility(View.GONE);
		}else{
			tv_no_excluded_downloads.setVisibility(View.VISIBLE);
		}
		
	}


	private static class ExcludedUpdate {
		private String name = "" ;
		private int vercode = 0;
		private String apkid = "";
		private boolean checked = false;
		
		public ExcludedUpdate(String name, String apkid, int vercode) {
			this.name = name;
			this.apkid = apkid;
			this.vercode = vercode;
		}
		
		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String getName() {
			return name;
		}

		public int getVercode() {
			return vercode;
		}

		public String getApkid() {
			return apkid;
		}

		public String toString(){
			return "Name: " + name + ", vercode: " + vercode + ", apkid: " + apkid;
		}
	}

	private static class ExcludedUpdatesHolder {
		public CheckBox cb_exclude;
		public TextView tv_name;
		public TextView tv_apkid;
		public TextView tv_vercode;
		public ExcludedUpdatesHolder(TextView tv_name, TextView tv_apkid, TextView tv_vercode, CheckBox cb_exclude) {
			this.cb_exclude = cb_exclude;
			this.tv_name = tv_name;
			this.tv_apkid = tv_apkid;
			this.tv_vercode = tv_vercode;
		}
	}

}
