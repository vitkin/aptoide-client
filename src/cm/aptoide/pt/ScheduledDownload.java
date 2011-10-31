package cm.aptoide.pt;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cm.aptoide.pt.BaseManagement.LstBinder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class ScheduledDownload extends ListActivity{
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	private MyCustomAdapter mAdapter;
	private DbHandler db = new DbHandler(this);
	private Vector<String> sch_list;

	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sch_list=db.getScheduledListNames();
        
        mAdapter = new MyCustomAdapter();
        for(int i =0;i!=sch_list.size();i++){
        	mAdapter.addName(sch_list.get(i));
        	mAdapter.addIconpath(getString(R.string.icons_path)+sch_list.get(i));
        }
        setListAdapter(mAdapter);
        
    }
 
    private class MyCustomAdapter extends BaseAdapter {
 
        private ArrayList<String> name = new ArrayList<String>();
        private ArrayList<String> iconpath = new ArrayList<String>();
        private ArrayList<String> version = new ArrayList<String>();
        
        private LayoutInflater mInflater;
 
        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addName(final String item) {
            name.add(item);
            notifyDataSetChanged();
        }
        
        public void addIconpath(final String item) {
            iconpath.add(item);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return name.size();
        }
 
        public String getItem(int position) {
            return name.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listicons, null);
                holder = new ViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.iconpath = (ImageView)convertView.findViewById(R.id.appicon);
                
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            
//            holder.version.setText(version.get(position));
            
            
            new Uri.Builder().build();
            holder.name.setText(name.get(position));
            holder.iconpath.setImageURI(Uri.parse(iconpath.get(position)));
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView name;
        public ImageView iconpath;
        public TextView version;
    }

}


