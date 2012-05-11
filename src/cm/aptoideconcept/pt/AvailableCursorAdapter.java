package cm.aptoideconcept.pt;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AvailableCursorAdapter extends CursorAdapter {

	DBHandler database;
	
	public AvailableCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context=context;
		imageLoader = new ImageLoader(context);
//		database= new DBHandler(context);
		
	}
	private ImageLoader imageLoader;
	private Context context;
	
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.app_name);
            holder.nameColumn = cursor.getColumnIndexOrThrow("name");
            holder.icon= (ImageView) view.findViewById(R.id.app_icon);
            holder.vername= (TextView) view.findViewById(R.id.installed_versionname);
            view.setTag(holder);
        }
        String s = cursor.getString(0);
        holder.name.setText(s);
        imageLoader.DisplayImage(cursor.getLong(4) , cursor.getString(cursor.getColumnIndex("icon")), holder.icon, arg1);
        holder.vername.setText(cursor.getString(3));
        
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return LayoutInflater.from(context).inflate(R.layout.app_row, null);
	}
	
	class ViewHolder{
		TextView name;
		int nameColumn;
		ImageView icon;
		TextView vername;
	}
	
	
	

}
