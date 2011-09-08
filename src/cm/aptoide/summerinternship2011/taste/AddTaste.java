/**
 * 
 */
package cm.aptoide.summerinternship2011.taste;

import java.math.BigInteger;
import java.util.ArrayList;

import cm.aptoide.pt.ApkInfo;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ApkInfo.WrapperUserTaste;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.ResponseToHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class AddTaste {
	
	private Context context;
	private String repo;
	private String apkid;
	private String version;
	private String user;
	private String password;
	private UserTaste userTaste;
	private ImageView like;
	private ImageView dislike;
	private TextView likes;
	private TextView dislikes;
	private ProgressDialog dialogProgress;
	private WrapperUserTaste userTastePrevious;
	private ApkInfo caller;
	
	/**
	 * 
	 * @param context
	 * @param repo
	 * @param apkid
	 * @param version
	 * @param user
	 * @param password
	 * @param userTaste
	 */
	public AddTaste(Context context, 
					String repo, 
					String apkid, 
					String version, 
					String user, 
					String password, 
					UserTaste userTaste,
					TextView likes,
					TextView dislikes,
					ImageView like,
					ImageView dislike,
					WrapperUserTaste tastePoster, 
					ApkInfo caller) {
		
		this.context = context;
		this.repo = repo;
		this.apkid = apkid;
		this.version = version;
		this.user = user;
		this.password = password;
		
		if( userTaste==null || (userTaste!=null && !userTaste.equals(UserTaste.LIKE) && !userTaste.equals(UserTaste.DONTLIKE) ) )
			throw new IllegalArgumentException();
		
		this.userTaste = userTaste;
		
		this.like = like;
		this.dislike = dislike;
		
		this.likes = likes;
		this.dislikes = dislikes;
		
		dialogProgress = null;
		
		this.userTastePrevious = tastePoster;
		this.caller = caller;
	}
	
	public void submit(){
		dialogProgress = ProgressDialog.show(context, context.getString(R.string.top_please_wait), context.getString(R.string.postingtaste),true);
		dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
		new SubmitTaste().execute();
	}
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	private class SubmitTaste extends AsyncTask<Void, Void, ResponseToHandler>{

		public SubmitTaste() {}
		
		@Override
		protected ResponseToHandler doInBackground(Void... args) {
			
			try {
				
				if(caller!=null){
					
					TasteGetter tasteGetter = new TasteGetter(repo, apkid, version);
					try {
						tasteGetter.parse(context, context.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE).getString( ConfigsAndUtils.LOGIN_USER_ID, null), this);
						synchronized(userTastePrevious){
							userTastePrevious.setValue(tasteGetter.getUserTaste());
						}
					}catch(Exception e){}
					
				}
				
				return Taste.sendTaste(context, repo, apkid, version, user, password, userTaste);
				
			} 
			//catch (IOException e) 					{} 
			//catch (ParserConfigurationException e) 	{} 
			//catch (SAXException e)					{}
			catch (Exception e)							{}
			
			return null;
			
		}
		
		@Override
		protected void onPostExecute(ResponseToHandler result) {
			
			
			
			
			synchronized(userTastePrevious){
				
				Log.d("Aptoide get2", userTastePrevious.getValue().toString());
				
				
				
				while(userTastePrevious.getOperatingThreads()!=0){
					try { 
						userTastePrevious.wait();
					} catch (InterruptedException e) {}
				}
				
				if(result!=null){ //No errors found
					
					if(result.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
						Toast.makeText(context, context.getString(R.string.opinionsuccess), Toast.LENGTH_LONG).show();
						
						
						switch(userTaste){
							case LIKE:
								if(!userTastePrevious.getValue().equals(UserTaste.LIKE)){
									if(userTastePrevious.getValue().equals(UserTaste.DONTLIKE)){
										dislike.setImageResource(R.drawable.dontlike);
										dislikes.setText(context.getString(R.string.dislikes)+new BigInteger(dislikes.getText().toString().replaceAll("\\D", "")).subtract(BigInteger.ONE).toString());
									}
									like.setImageResource(R.drawable.likehover);
									likes.setText(context.getString(R.string.likes)+new BigInteger(likes.getText().toString().replaceAll("\\D", "")).add(BigInteger.ONE).toString());
								}
								break;
							case DONTLIKE: 
								if(!userTastePrevious.getValue().equals(UserTaste.DONTLIKE)){
									if(userTastePrevious.getValue().equals(UserTaste.LIKE)){
										like.setImageResource(R.drawable.like);
										likes.setText(context.getString(R.string.likes)+new BigInteger(likes.getText().toString().replaceAll("\\D", "")).subtract(BigInteger.ONE).toString());
									}
									dislike.setImageResource(R.drawable.dontlikehover);
									dislikes.setText(context.getString(R.string.dislikes)+new BigInteger(dislikes.getText().toString().replaceAll("\\D", "")).add(BigInteger.ONE).toString());
								}
								break;
							default: break;
						}
						
						userTastePrevious.setValue(userTaste);
						
					}else{
						
						ArrayList<String> errors = result.getErrors();
						for(String error: errors){
							Toast.makeText(context, error, Toast.LENGTH_LONG).show();
						}
						
					}
					
				} else { 
					//Some error was found
					Toast.makeText(context, context.getString(R.string.unabletoexecutecheknet), Toast.LENGTH_LONG).show();
				}
				
				dialogProgress.dismiss();
			
			}
			
		}
		
	}
	
}
