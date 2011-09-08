/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;

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
 * @param <T>
 */
public class CommentsAdapter<T extends Comment> extends ArrayAdapter<T>  {
	
	/**
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public CommentsAdapter(Activity context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
	}

	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			convertView = inflater.inflate(R.layout.commentlistviewitem, null);
		}
		
		TextView author = (TextView) convertView.findViewById(R.id.author);
		TextView subject = (TextView) convertView.findViewById(R.id.subject);
		TextView date = (TextView) convertView.findViewById(R.id.date);
		TextView content = (TextView) convertView.findViewById(R.id.content);
		
		T currentEntry = super.getItem(position);
		
		author.setText(currentEntry.getUsername());
		subject.setText(currentEntry.getSubject());
		date.setText(ConfigsAndUtils.TIME_STAMP_FORMAT.format(currentEntry.getTimestamp()));
		content.setText(currentEntry.getText());
		
		return convertView;
		
	}
	
//	/**
//	 * 
//	 * @see android.widget.ArrayAdapter#getPosition(java.lang.Object)
//	 */
//	@Override
//	public int getPosition(T item) {
//		for(int i = 0; i<this.getCount();i++ ){
//			if(item.equals(this.getItem(i))) 
//				return i;
//		}
//		return -1;
//	}
	
	public ArrayList<T> removeAll(){
		
		ArrayList<T> buffer = new ArrayList<T>();
		while(getCount()!=0){
			T obj = getItem(0);
			buffer.add(obj);
			this.remove(obj);
		}
		return buffer;
	}
	
	/**
	 * 
	 * @param itens ArrayList<T> to add at the begin of the adapter
	 */
	public void addAtBegin(ArrayList<T> itens){
		itens.addAll(removeAll());
		int i = 0;
		while(i!=itens.size()){
			this.add(itens.get(i++));
		}
	}
	
//	/**
//	 * @see android.widget.BaseAdapter#isEnabled(int)
//	 */
//	@Override
//	public boolean isEnabled(int position) {
//		return false;
//	}
	
}
