/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.ResponseToHandler;
import cm.aptoide.summerinternship2011.SetBlank;
import cm.aptoide.summerinternship2011.credentials.Login;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
 * @author rafael
 * @since summerinternship2011
 * 
 */
public class AddCommentDialog extends Dialog implements OnDismissListener{
	
	
	private final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
	
	private Comment replyTo;
	
	private EditText subject;
	private EditText body;
	
	private String repo;
	private String apkid;
	private String version;
	
	private LoadOnScrollCommentList loadOnScrollComList;
	private ProgressDialog dialogProgress;
	
	/**
	 * 
	 * @param context
	 * @param loadOnScrollComList
	 * @param replyTo
	 * @param repo
	 * @param apkid
	 * @param version
	 */
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
	
	/**
	 * 
	 */
	public void postMessage(){
		
		dialogProgress = ProgressDialog.show(getContext(), getContext().getString(R.string.top_please_wait),getContext().getString(R.string.postingcomment),true);
		dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
		this.hide();
		
		String username = sharedPreferences.getString("usernameLogin", null);
		String passwordSha1 = sharedPreferences.getString("passwordLogin", null);
		if(username != null && passwordSha1!=null){
			
			/**
			 * @author rafael
			 * @since summerinternship2011
			 * 
			 */
			class PostComment extends AsyncTask<Void, Void, ResponseToHandler>{
				
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
				protected ResponseToHandler doInBackground(Void... params) {
					
					try {
						return Comment.sendComment(AddCommentDialog.this.getContext(), 
								repo, 
								apkid, 
								version, 
								((SetBlank)subject.getOnFocusChangeListener()).getAlreadySetted()?subject.getText().toString():null,
								(replyTo!=null?(AddCommentDialog.this.getContext().getString(R.string.inresponseto)+replyTo.getUsername())+ConfigsAndUtils.LINE_SEPARATOR:"")+body.getText().toString(), 
								username, 
								passwordSha1,
								replyTo!=null?replyTo.getId():null);
					} catch (Exception e){}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(ResponseToHandler result) {
					dialogProgress.dismiss();
					if(result!=null){
						loadOnScrollComList.fetchNewComments();
						AddCommentDialog.this.dismiss();
						if(result.getStatus().equals(cm.aptoide.summerinternship2011.Status.FAIL)){
							for(String error: result.getErrors()){
								Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(AddCommentDialog.this.getContext(), getContext().getString(R.string.commentadded), Toast.LENGTH_LONG).show();
						}
						
					}else{
						Toast.makeText(getContext(), getContext().getString(R.string.unabletoexecutecheknet), Toast.LENGTH_LONG).show();
					}
				}
				
			}
			
			new PostComment(username, passwordSha1, repo, apkid, version).execute();
			
		}
	}
	
	public void onDismiss(DialogInterface dialog) {	
		if(dialog==null || ((Login)dialog).isLoginSubmited()){
			postMessage();
		}
	}
	
}
