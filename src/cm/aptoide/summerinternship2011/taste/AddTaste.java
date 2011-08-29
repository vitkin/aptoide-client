/**
 * 
 */
package cm.aptoide.summerinternship2011.taste;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cm.aptoide.summerinternship2011.ResponseToHandler;

import android.content.Context;
import android.os.AsyncTask;
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
					UserTaste userTaste) {
		
		this.context = context;
		this.repo = repo;
		this.apkid = apkid;
		this.version = version;
		this.user = user;
		this.password = password;
		
		if( userTaste!=null && !userTaste.equals(UserTaste.LIKE) && !userTaste.equals(UserTaste.DONTLIKE) )
			throw new IllegalArgumentException();
		
		this.userTaste = userTaste;
		
		new SubmitTaste().execute();
		
	}
	
	/**
	 * 
	 * @author rafael
	 *
	 */
	private class SubmitTaste extends AsyncTask<Void, Void, ResponseToHandler>{

		public SubmitTaste() {}
		
		@Override
		protected ResponseToHandler doInBackground(Void... args) {
			
			try {
				return Taste.sendTaste(context, repo, apkid, version, user, password, userTaste);
			} 
			catch (IOException e) {} 
			catch (ParserConfigurationException e) {} 
			catch (SAXException e) {}
			catch (Exception e){}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseToHandler result) {
			if(result!=null){
				if(result.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
					
					
					
					
				}else{
					for(String error: result.getErrors()){
						Toast.makeText(context, error, Toast.LENGTH_LONG);
					}
				}
			}
		}
		
	}
	
	
}
