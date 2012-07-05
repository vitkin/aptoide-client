package cm.aptoide.pt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cm.aptoide.pt.utils.SetBlankOnFocusChangeListener;
import cm.aptoide.pt.webservices.ResponseHandler;
import cm.aptoide.pt.webservices.comments.AddCommentDialog;
import cm.aptoide.pt.webservices.comments.Comment;
import cm.aptoide.pt.webservices.login.Algorithms;
import cm.aptoide.pt.webservices.login.LoginDialog;
import cm.aptoide.pt.webservices.taste.WrapperUserTaste;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddComment extends FragmentActivity implements OnDismissListener{

	private String repo;
	private String apkid;
	private String vername;
	private EditText name;
	private EditText comment;
	private Button submit;
	SharedPreferences sharedPreferences;
	
	private ProgressDialog dialogProgress;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.addcomment);
		repo = getIntent().getStringExtra("repo");
		apkid = getIntent().getStringExtra("apkid");
		vername = getIntent().getStringExtra("vername");
		
		name = (EditText) findViewById(R.id.name);
		comment = (EditText) findViewById(R.id.comment);
		submit = (Button) findViewById(R.id.submitComment);
		
		submit.setOnClickListener(submitCommentListener);
		sharedPreferences = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		if(sharedPreferences.getString("userName", null)==null){
			name.setVisibility(View.VISIBLE);
			if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null)
			name.setText(sharedPreferences.getString(Configs.LOGIN_USER_NAME, "").split("@")[0]);
		}else{
			name.setVisibility(View.GONE);
		}
		
	}
	LoginDialog loginComments;
	private OnClickListener submitCommentListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(comment.getText().toString().length()!=0){
				//If the text as some content on it provided by the user
				if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){
					loginComments = new LoginDialog(AddComment.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, null, null, repo, apkid, vername, null, new WrapperUserTaste());
					loginComments.setOnDismissListener(AddComment.this);
					loginComments.show();
				}else{ 
					
					postMessage();
					
				}
			} else {
				Toast.makeText(AddComment.this, AddComment.this.getString(R.string.enterbody), Toast.LENGTH_LONG).show();
			}
		
		}
	};
	public void onDismiss(DialogInterface dialog) {
		if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)!=null)
			name.setText(sharedPreferences.getString(Configs.LOGIN_USER_NAME, "").split("@")[0]);
	}
	
	
	public void postMessage(){
		
		dialogProgress = ProgressDialog.show(this, this.getString(R.string.please_wait),this.getString(R.string.postingcomment),true);
		dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
		
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
						if(name.getText().toString().length()>0){
							updateName(name.getText().toString());
						}
						ResponseHandler handler = Comment.sendComment(AddComment.this, 
								repo, 
								apkid, 
								version, 
								comment.getText().toString(), 
								username, 
								passwordSha1);
						
						
						
						return handler;
						
					} catch (Exception e){}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(ResponseHandler result) {
					dialogProgress.dismiss();
					if(result!=null){
						
						if(result.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.FAIL)){
							for(String error: result.getErrors()){
								Toast.makeText(AddComment.this, error, Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(AddComment.this, AddComment.this.getString(R.string.commentadded), Toast.LENGTH_LONG).show();
						}
						
					}else{
						Toast.makeText(AddComment.this, AddComment.this.getString(R.string.unabletoexecutecheknet), Toast.LENGTH_LONG).show();
					}
					setResult(Activity.RESULT_OK);
					finish();
				}
				
			}
			
			new PostComment(username, passwordSha1, repo, apkid, vername).execute();
			
		}
	}
	public void updateName(String name) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, ClientProtocolException, IOException, JSONException {
		String passwordSha1 = sharedPreferences.getString(Configs.LOGIN_PASSWORD, null);
		String email = sharedPreferences.getString(Configs.LOGIN_USER_NAME, null);
		String hmac = Algorithms.computeHmacSha1(email+passwordSha1+name+1, "bazaar_hmac");
		HttpPost post = new HttpPost("https://www.bazaarandroid.com/webservices/createUser");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("passhash", passwordSha1));
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("update", "1"));
		nameValuePairs.add(new BasicNameValuePair("hmac", hmac));
		nameValuePairs.add(new BasicNameValuePair("mode", "json"));
		
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();

		String responseText = EntityUtils.toString(entity);
		final JSONObject json = new JSONObject(responseText);
		if(json.has("errors")){
			runOnUiThread(new Runnable() {
				
				public void run() {
					Toast.makeText(AddComment.this, "errors occurred", 1).show();
					
				}
			});
			
		}else{
			Editor sPrefeditor = sharedPreferences.edit();
			sPrefeditor.putString("userName", name);
			sPrefeditor.commit();
		}
		
		
	}


	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		
		super.onActivityResult(arg0, arg1, intent);
		if(intent.hasExtra("username")){
			String username = intent.getStringExtra("username");
			loginComments.dismiss();
			if(name.getText().length()==0){
				name.setText(username.split("@")[0]);
			}
			
		}
		
	}
	
}

