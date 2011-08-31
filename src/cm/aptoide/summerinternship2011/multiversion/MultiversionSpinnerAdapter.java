package cm.aptoide.summerinternship2011.multiversion;

import java.util.List;

import cm.aptoide.pt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * The adapter of the spinner responsible for showing the available versions.
 */
public class MultiversionSpinnerAdapter<T extends VersionApk> extends ArrayAdapter<T> {
	
	private String versionLabel;
	private String sizeLabel;
	
	public MultiversionSpinnerAdapter(Activity context, int textViewResourceId,
			List<T> objects,String versionLabel, String sizeLabel) {
		super(context, textViewResourceId, objects);
		this.versionLabel = versionLabel;
		this.sizeLabel = sizeLabel;
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView defaultView = ((TextView)super.getView(position, convertView, parent));
		defaultView.setText(versionLabel+" "+super.getItem(position).getVersion());
		return defaultView;
	}

	/**
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			convertView = inflater.inflate(R.layout.multiversionspinneritem, null);
		}
		
		TextView multiVersionItemVersion = (TextView) convertView.findViewById(R.id.versionspinnermultiversion);
		TextView multiVersionItemSize = (TextView) convertView.findViewById(R.id.sizespinnermultiversion);
		T currentEntry = super.getItem(position);
		
		multiVersionItemVersion.setText(versionLabel+" "+currentEntry.getVersion());
		multiVersionItemSize.setText(sizeLabel + " " + currentEntry.getSize() + " kb");
		return convertView;
	
	}

	/**
	 * 
	 * @see android.widget.ArrayAdapter#getPosition(java.lang.Object)
	 */
	@Override
	public int getPosition(T item) {
		for(int i = 0; i<this.getCount();i++ ){
			if(item.equals(this.getItem(i))) return i;
		}
		return -1;
	}
	
}
