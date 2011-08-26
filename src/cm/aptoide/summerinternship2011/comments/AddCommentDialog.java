/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.SetBlank;
import cm.aptoide.summerinternship2011.credentials.Login;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
	private LoadOnScrollCommentList loadOnScrollComList;
	
	public AddCommentDialog(Activity context, LoadOnScrollCommentList loadOnScrollComList, Comment replyTo, String repo, String apkid, String version) {
		super(context);
		this.replyTo = replyTo;
		this.repo = repo;
		this.apkid = apkid; 
		this.version = version;
		this.loadOnScrollComList = loadOnScrollComList; 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.addcomment);
		
		this.setTitle(getContext().getString(R.string.commentlabel));
		
		if(replyTo!=null){
			TextView inresponse = ((TextView)findViewById(R.id.inresponseto));
			inresponse.append(replyTo.getUsername());
			inresponse.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
			inresponse.setLayoutParams(inresponse.getLayoutParams());
		}
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
						Comment.sendComment(AddCommentDialog.this.getContext(), 
								repo, 
								apkid, 
								version, 
								((SetBlank)subject.getOnFocusChangeListener()).getAlreadySetted()?subject.getText().toString():null,
								(replyTo!=null?(AddCommentDialog.this.getContext().getString(R.string.inresponseto)+replyTo.getUsername())+ConfigsAndUtils.LINE_SEPARATOR:"")+body.getText().toString(), 
								username, 
								passwordSha1,
								replyTo!=null?replyTo.getId():null);
					} catch (Exception e){ return false; }
					return true;
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					if(result){
						
						loadOnScrollComList.fetchNewComments();
						
						AddCommentDialog.this.dismiss();
						Toast.makeText(AddCommentDialog.this.getContext(), getContext().getString(R.string.commentadded), Toast.LENGTH_LONG).show();
						
					}else{
						Toast.makeText(getContext(), getContext().getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
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
