package cm.aptoide.pt.webservices.comments;

import cm.aptoide.pt.Configs;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ApkInfo.WrapperUserTaste;
import cm.aptoide.pt.utils.SetBlankOnFocusChangeListener;
import cm.aptoide.pt.webservices.ResponseHandler;
import cm.aptoide.pt.webservices.login.LoginDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Structure of the webservice POST:
 * 	URL: http://www.bazaarandroid.com/webservices/addApkComment
 * 	Variables:
 *		repo 				- Repository name
 *		apkid 				- Application package ID (example: com.mystuff.android.myapp)
 *		apkversion 			- Application version (example: 1.4.2)
 *		text 				- Comment text
 *		mode 				- Return mode/format ('xml' or 'json')
 *		lang (optional) 	- Language code of the comment: en_GB, pt_PT, es_ES, fr_FR, de_DE, it_IT, zu_RU, zh_CN, ko_KR
 *		subject (optional) 	- Comment subject/title
 *		answerto (optional) - ID of the comment this comment is replying to
 * 	Response:
 * 		status 				- Request result status (OK or FAIL)
 * 		errors 				- Errors log from the request (not existent when status="OK")
 */
public class AddCommentDialog extends Dialog implements OnDismissListener{
	
	
	private final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
	
	private Comment replyTo;
	
	private EditText subject;
	private EditText body;
	
	private String repo;
	private String apkid;
	private String version;
	
	private CommentPosterListOnScrollListener loadOnScrollComList;
	private ProgressDialog dialogProgress;
	private ImageView like;
	private ImageView dislike;
	
	private WrapperUserTaste userTasteGetter;
	
	/**
	 * 
	 * @param context
	 * @param loadOnScrollComList
	 * @param replyTo
	 * @param repo
	 * @param apkid
	 * @param version
	 */
	public AddCommentDialog(Activity context, CommentPosterListOnScrollListener loadOnScrollComList, Comment replyTo, 
						ImageView like, ImageView dislike, String repo, String apkid, String version, WrapperUserTaste userTasteGetter) {
		super(context);
		this.replyTo = replyTo;
		this.loadOnScrollComList = loadOnScrollComList; 
		this.repo = repo;
		this.apkid = apkid; 
		this.version = version;
		this.like = like;
		this.dislike = dislike;
		this.userTasteGetter = userTasteGetter;
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
				if(body.getText().toString().length()!=0 && ((SetBlankOnFocusChangeListener)body.getOnFocusChangeListener()).getAlreadySetted()){
					//If the text as some content on it provided by the user
					if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){
						
						
						LoginDialog loginComments = new LoginDialog(AddCommentDialog.this.getContext(), LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, dislike, repo, apkid, version, null, userTasteGetter);
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
		
		body.setOnFocusChangeListener(new SetBlankOnFocusChangeListener());
		subject.setOnFocusChangeListener(new SetBlankOnFocusChangeListener());
		
	}
	
	/**
	 * 
	 */
	public void postMessage(){
		
		dialogProgress = ProgressDialog.show(getContext(), getContext().getString(R.string.top_please_wait),getContext().getString(R.string.postingcomment),true);
		dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
		this.hide();
		
		String username = sharedPreferences.getString(Configs.LOGIN_USER_NAME, null);
		String passwordSha1 = sharedPreferences.getString(Configs.LOGIN_PASSWORD, null);
		if(username != null && passwordSha1!=null){
			
			/**
			 * @author rafael
			 * @since summerinternship2011
			 * 
			 */
			class PostComment extends AsyncTask<Void, Void, ResponseHandler>{
				
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
				protected ResponseHandler doInBackground(Void... params) {
					
					try {
						return Comment.sendComment(AddCommentDialog.this.getContext(), 
								repo, 
								apkid, 
								version, 
								((SetBlankOnFocusChangeListener)subject.getOnFocusChangeListener()).getAlreadySetted()?subject.getText().toString():null,
								(replyTo!=null?(AddCommentDialog.this.getContext().getString(R.string.inresponseto)+replyTo.getUsername())+Configs.LINE_SEPARATOR:"")+body.getText().toString(), 
								username, 
								passwordSha1,
								replyTo!=null?replyTo.getId():null);
					} catch (Exception e){}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(ResponseHandler result) {
					dialogProgress.dismiss();
					if(result!=null){
						if(loadOnScrollComList!=null)
							loadOnScrollComList.fetchNewComments();
						AddCommentDialog.this.dismiss();
						if(result.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.FAIL)){
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
		if(dialog==null || ((LoginDialog)dialog).isLoginSubmited()){
			postMessage();
		}
	}
	
}
