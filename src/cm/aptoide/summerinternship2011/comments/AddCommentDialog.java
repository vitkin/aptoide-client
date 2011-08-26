/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.SetBlank;
import cm.aptoide.summerinternship2011.credentials.Login;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author rafael
 *
 */
public class AddCommentDialog extends Dialog implements OnDismissListener{
	
	private Comment replyTo;
	private final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
	
	private EditText subject;
	private EditText body;
	
	private String repo;
	private String apkid;
	private String version;
	
	public AddCommentDialog(Context context, Comment replyTo, String repo, String apkid, String version) {
		super(context);
		this.replyTo = replyTo;
		this.repo = repo;
		this.apkid = apkid; 
		this.version = version;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.addcomment);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titledialog);
		
		this.setTitle(getContext().getString(R.string.commentlabel)+(replyTo!=null?" "+getContext().getString(R.string.inresponseto)+" "+replyTo.getUsername():""));
		
		body = ((EditText)findViewById(R.id.comment));
		subject = ((EditText)findViewById(R.id.subject));
		
		((Button)findViewById(R.id.clearReply)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				((EditText)findViewById(R.id.subject)).setText("");
				((EditText)findViewById(R.id.comment)).setText("");
			}
		});
		
		final Button submit = ((Button)findViewById(R.id.submitComment));
		submit.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				if(body.getText().toString().length()!=0 && ((SetBlank)body.getOnFocusChangeListener()).getAlreadySetted()){
					//If the text as some content on it provided by the user
					if(sharedPreferences.getString("usernameLogin", null)==null || sharedPreferences.getString("passwordLogin", null)==null){				
						Login loginComments = new Login(AddCommentDialog.this.getContext(), Login.InvoqueNature.NO_CREDENTIALS_SET);
						loginComments.setOnDismissListener(AddCommentDialog.this);
						loginComments.show();
					}else{
						postMessage();
					}
				} else {
					Toast.makeText(AddCommentDialog.this.getContext(), AddCommentDialog.this.getContext().getString(R.string.enterbody), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		body.setOnFocusChangeListener(new SetBlank());
		subject.setOnFocusChangeListener(new SetBlank());
	}
	
	public void postMessage(){
		String username = sharedPreferences.getString("usernameLogin", null);
		String passwordSha1 = sharedPreferences.getString("passwordLogin", null);
		if(username != null && passwordSha1!=null){
			
			
			class PostComment extends AsyncTask<Void, Void, Boolean>{
				
				private String username;
				private String passwordSha1;
				
				private String repo;
				private String apkid;
				private String version;
				
				public PostComment(String username, String passwordSha1, String repo, String apkid, String version){
					this.username = username;
					this.passwordSha1 = passwordSha1;
					
					this.repo = repo;
					this.apkid = apkid;
					this.version = version;
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					try {
						//Comment.sendComment(this, apk_repo_str.substring("http://".length(),apk_repo_str.indexOf(".bazaarandroid.com")), apk_id, apk_ver_str.replaceAll("[^0-9\\.]", ""), ((EditText)findViewById(R.id.comment)).getText().toString(), sharedPreferences.getString("usernameLogin", null), sharedPreferences.getString("passwordLogin", null));
						Comment.sendComment(AddCommentDialog.this.getContext(), 
								repo, 
								apkid, 
								version, 
								((SetBlank)subject.getOnFocusChangeListener()).getAlreadySetted()?subject.getText().toString():null,
								body.getText().toString(), 
								username, 
								passwordSha1,
								replyTo!=null?replyTo.getId():null);
					} catch (Exception e){ return false; }
					return true;
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					if(result){
						AddCommentDialog.this.dismiss();
						//Toast.makeText(AddCommentDialog.this.getContext(), getContext().getString(R.string.commentadded), Toast.LENGTH_LONG).show();
					}else{
						//Toast.makeText(getContext(), getContext().getString(R.string.unabletoexecutelogreq), Toast.LENGTH_LONG).show();
					}
				}
				
			}
			
			new PostComment(username, passwordSha1, repo, apkid, version).execute();
			
		}
	}
	
	public void onDismiss(DialogInterface dialog) {	
		postMessage();
	}
	
}
