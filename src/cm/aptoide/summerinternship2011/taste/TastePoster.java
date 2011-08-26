package cm.aptoide.summerinternship2011.taste;

import java.util.HashMap;

import cm.aptoide.pt.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class TastePoster extends AsyncTask<Void, Integer, HashMap<String,String>>{
	
	private String apkid;
	private String version;
	private String repo;
	private TextView likes;
	private TextView dislikes;
	private Context context;
	
	/**
	 * 
	 * @param context
	 * @param apkid
	 * @param version
	 * @param repo
	 * @param likes
	 * @param dislikes
	 */
	public TastePoster(Context context, String apkid, String version, String repo, TextView likes, TextView dislikes) {
		this.apkid = apkid;
		this.version = version;
		this.repo = repo;
		this.likes = likes;
		this.dislikes = dislikes;
		this.context = context;
	}
	
	@Override
	protected HashMap<String, String> doInBackground(Void... params) {
		
		try {
			HashMap<String,String> hashMapTaste = new HashMap<String,String>();
			TasteGetter tasteGetter = new TasteGetter(repo,apkid,version);
			tasteGetter.parse(context, null);
			if(tasteGetter.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
				hashMapTaste.put("LIKES",tasteGetter.getLikes().toString());
				hashMapTaste.put("DISLIKES",tasteGetter.getDislikes().toString());
				return hashMapTaste;
			}
		} catch(Exception e){}
		
		return null;
	}
	
	protected void onPostExecute(HashMap<String,String> result) {
		if(result!=null){
			likes.setText(context.getString(R.string.likes)+result.get("LIKES"));
			dislikes.setText(context.getString(R.string.dislikes)+result.get("DISLIKES"));
		}else{
			likes.getLayoutParams().height=0;
			dislikes.getLayoutParams().height=0;
		}
    }
	
}
