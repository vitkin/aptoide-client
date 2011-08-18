/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

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
 * @param <T>
 */
public class CommentsAdapter<T extends Comment> extends ArrayAdapter<T>  {
	
	public CommentsAdapter(Activity context, int textViewResourceId,
			List<T> objects) {
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
		date.setText(Comment.timeStampFormat.format(currentEntry.getTimestamp()));
		content.setText(currentEntry.getText());
		
		return convertView;
		
	}
	
	/**
	 * 
	 * @see android.widget.ArrayAdapter#getPosition(java.lang.Object)
	 */
	@Override
	public int getPosition(T item) {
		for(int i = 0; i<this.getCount();i++ ){
			if(item.equals(this.getItem(i))) 
				return i;
		}
		return -1;
	}
	
//	/**
//	 * @see android.widget.BaseAdapter#isEnabled(int)
//	 */
//	@Override
//	public boolean isEnabled(int position) {
//		return false;
//	}
	
}
