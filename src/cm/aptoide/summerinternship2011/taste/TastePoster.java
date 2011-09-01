package cm.aptoide.summerinternship2011.taste;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cm.aptoide.pt.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class TastePoster extends AsyncTask<Void, Integer, TasteGetter>{
	
	private Context context;
	private String apkid;
	private String version;
	private String repo;
	private TextView likes;
	private TextView dislikes;
	private ImageView like;
	private ImageView dislike;
	private String useridLogin;
	
	/**
	 * 
	 * @param context
	 * @param apkid
	 * @param version
	 * @param repo
	 * @param likes
	 * @param dislikes
	 */
	public TastePoster(Context context,String apkid, String version, String repo, TextView likes, TextView dislikes, ImageView like, ImageView dislike, String useridLogin) {
		this.apkid = apkid;
		this.version = version;
		this.repo = repo;
		this.likes = likes;
		this.dislikes = dislikes;
		this.context = context;
		this.like = like;
		this.dislike = dislike;
		this.useridLogin = useridLogin;
	}
	
	@Override
	protected TasteGetter doInBackground(Void... params) {
		
		TasteGetter tasteGetter = new TasteGetter(repo,apkid,version);
		
		try {
			tasteGetter.parse(context, useridLogin);
			return tasteGetter;
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (Exception e){}
		
		
		return null;
		
	}
	
	protected void onPostExecute(TasteGetter result) {
		if(result!=null){
			
			likes.setText(context.getString(R.string.likes)+result.getLikes().toString());
			dislikes.setText(context.getString(R.string.dislikes)+result.getDislikes().toString());
			
			switch(result.getUserTaste()){
				case LIKE:
					like.setImageResource(R.drawable.likehover);
					break;
				case DONTLIKE: 
					dislike.setImageResource(R.drawable.dontlikehover);
					break;
				default: break;
			}
			
		}else{
			likes.getLayoutParams().height=0;
			dislikes.getLayoutParams().height=0;
		}
    }
	
}
