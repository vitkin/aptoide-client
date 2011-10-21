package cm.aptoide.pt.multiversion;

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
 * @since 2.5.3
 * 
 * The adapter of the spinner responsible for showing the available versions.
 */
public class MultiversionSpinnerAdapter<T extends VersionApk> extends ArrayAdapter<T> {
	
	private String versionLabel;
	private String sizeLabel;
	private String downLabel;
	
	public MultiversionSpinnerAdapter(Activity context, int textViewResourceId,
			List<T> objects,String versionLabel, String sizeLabel, String downLabel) {
		super(context, textViewResourceId, objects);
		this.versionLabel = versionLabel;
		this.sizeLabel = sizeLabel;
		this.downLabel = downLabel;
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			convertView = inflater.inflate(R.layout.infomultiversionspinner, null);
		}
		
		TextViewFocused multiVersionItemVersion = (TextViewFocused) convertView.findViewById(R.id.versionspinnermultiversionSelected);
		TextView multiVersionItemInfo = (TextView) convertView.findViewById(R.id.infopinnermultiversionSelected);
		T currentEntry = super.getItem(position);
		
		multiVersionItemVersion.setText(
				versionLabel+" "+currentEntry.getVersion());
		multiVersionItemInfo.setText(
				formatSize(currentEntry)+", "+formatDownloads(currentEntry));
		
		return convertView;
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
		TextView multiVersionItemDown = (TextView) convertView.findViewById(R.id.downspinnermultiversion);
		T currentEntry = super.getItem(position);
		
		multiVersionItemVersion.setText(versionLabel+" "+currentEntry.getVersion());
		multiVersionItemSize.setText(formatSize(currentEntry));
		multiVersionItemDown.setText(formatDownloads(currentEntry));
		
		return convertView;
	
	}
	
	private String formatSize(VersionApk version){
		return sizeLabel + " " + ((version.getSize()>0)?(version.getSize()+ " kb"):"not available") ;
	}
	
	private String formatDownloads(VersionApk version){
		return downLabel + " " + ((version.getDownloads()>=0)?version.getDownloads():"not available");
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
