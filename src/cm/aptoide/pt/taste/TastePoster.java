package cm.aptoide.pt.taste;

import cm.aptoide.pt.R;
import cm.aptoide.pt.ApkInfo.WrapperUserTaste;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author rafael
 * @since 2.5.3
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
	private WrapperUserTaste userTaste;
	
	/**
	 * 
	 * @param context
	 * @param apkid
	 * @param version
	 * @param repo
	 * @param likes
	 * @param dislikes
	 */
	public TastePoster(Context context,String apkid, String version, String repo, 
						TextView likes, TextView dislikes, ImageView like, 
						ImageView dislike, String useridLogin, WrapperUserTaste userTaste) {
		this.apkid = apkid;
		this.version = version;
		this.repo = repo;
		this.likes = likes;
		this.dislikes = dislikes;
		this.context = context;
		this.like = like;
		this.dislike = dislike;
		this.useridLogin = useridLogin;
		this.userTaste = userTaste;
	}
	
	@Override
	protected TasteGetter doInBackground(Void... params) {
		
		synchronized(userTaste){
			userTaste.incOperatingThreads();
		}
		
		TasteGetter tasteGetter = new TasteGetter(repo, apkid, version);
		
		try {
			tasteGetter.parse(context, useridLogin, this);
			return tasteGetter;
		}
		//catch (ParserConfigurationException e) 		{} 
		//catch (SAXException e) 						{} 
		//catch (IOException e) 						{} 
		catch (Exception e)								{}
		
		return null;
		
	}
	
	protected void onPostExecute(TasteGetter result) {
		
		synchronized(userTaste){
			
			if(!this.isCancelled()){
				if(result!=null){ //Everything run as expected
					
					dislike.setImageResource(R.drawable.dontlike);
					like.setImageResource(R.drawable.like);
					
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
					
					userTaste.setValue(result.getUserTaste()); //UserTaste.NOTEVALUATED can not happen hear
					
				}else{ //There was some error
					
					likes.setText(context.getText(R.string.tastenotavailable));
					dislikes.setText("");
					userTaste.setValue(EnumUserTaste.NOTEVALUATED);
					
				}
				userTaste.notifyAll();
			}
			
			//Relese license
			userTaste.decOperatingThreads();
			
		}
		
    }
	
	@Override
	protected void onCancelled() {
		synchronized(userTaste){
			userTaste.decOperatingThreads();
		}
	}
	
}
