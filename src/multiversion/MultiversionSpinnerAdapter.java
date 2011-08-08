package multiversion;

import java.util.List;

import cm.aptoide.pt.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author rafael
 * 
 * 
 * 
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
		
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		View spinnerEntry = inflater.inflate(R.layout.multiversionspinneritem, null);
		TextView multiVersionItemVersion = (TextView) spinnerEntry.findViewById(R.id.versionspinnermultiversion);
		TextView multiVersionItemSize = (TextView) spinnerEntry.findViewById(R.id.sizespinnermultiversion);
		T currentEntry = super.getItem(position);
		
		multiVersionItemVersion.setText(versionLabel+" "+currentEntry.getVersion());
		multiVersionItemSize.setText(sizeLabel + " " + currentEntry.getSize() + " kb");
		return spinnerEntry;
	
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
