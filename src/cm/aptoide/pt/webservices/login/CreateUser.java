package cm.aptoide.pt.webservices.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import cm.aptoide.pt.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateUser extends FragmentActivity {

	View stub = null;
	private String password;
	private String email;
	private String passwordConfirm;
	
	private OnClickListener submitCreateUserListener = new OnClickListener() {
		
		
		

		public void onClick(View v) {
			
			
			password=((EditText) findViewById(R.id.password_box)).getText().toString();
			passwordConfirm=((EditText) findViewById(R.id.password_box_confirm)).getText().toString();
			email=((EditText) findViewById(R.id.email_box)).getText().toString();
			
			if(password.equals(passwordConfirm)){
			
			new Thread(new Runnable() {
				
				public void run() {
					submit();
					
				}

				
			}).start();
			}else{
				if(stub==null){
					stub = ((ViewStub) findViewById(R.id.viewStub1)).inflate();
				}
				stub.setVisibility(View.VISIBLE);
				((TextView)stub.findViewById(R.id.error)).setText("Passwords does not match");
			}
			
	};};
	
	private void submit() {
		try{
			
			String sha1 = Algorithms.computeSHA1sum(password);
			String hmac;
			if(password.length()>0){
				hmac = Algorithms.computeHmacSha1(email+sha1, "bazaar_hmac");
			}else{
				return;
			}
			
			HttpPost post = new HttpPost("https://www.bazaarandroid.com/webservices/createUser");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			if(password.length()>0){
				nameValuePairs.add(new BasicNameValuePair("passhash", sha1));
			}
			
			nameValuePairs.add(new BasicNameValuePair("hmac", hmac));
			nameValuePairs.add(new BasicNameValuePair("mode", "json"));
			
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();

			String responseText = EntityUtils.toString(entity);
			final JSONObject json = new JSONObject(responseText);
			
//			Toast.makeText(CreateUserActivity.this, string =(json.has("errors")?json.getString("errors"):json.getString("status")) , 1).show();
			runOnUiThread(new Runnable() {
				
				public void run() {
					try{
						String string;
						if(stub==null){
							stub = ((ViewStub) findViewById(R.id.viewStub1)).inflate();
						}
						stub.setVisibility(View.VISIBLE);
						if(json.getString("status").equals("OK")){
							Toast.makeText(CreateUser.this, "Account successfuly created.", 1).show();
							finish();
						}
						((TextView)stub.findViewById(R.id.error)).setText(string =(json.has("errors")?json.getString("errors"):json.getString("status")));
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
	}
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.createuser);
		findViewById(R.id.submitCreateUser).setOnClickListener(submitCreateUserListener );
	}
}
