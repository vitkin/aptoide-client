package cm.aptoide.pt2.webservices.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cm.aptoide.pt2.Configs;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.util.Algorithms;

public class Login extends Activity {
	ProgressDialog pd;
	EditText username_box;
	EditText password_box;
	String username;
	String password;
	static Context context;
	private boolean succeed = false;
	private static SharedPreferences sPref; 
	private static SharedPreferences.Editor prefEdit;
	public final static int REQUESTCODE = 10;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
		context = this;
		username_box = (EditText) findViewById(R.id.username);
		password_box = (EditText) findViewById(R.id.password);
		pd = new ProgressDialog(context);
		if(isLoggedIn(context)){
			username_box.setText(getUserLogin(this));
		}else if(sPref.getString(Configs.LOGIN_USER_LOGIN, null)!=null){
			username= sPref.getString(Configs.LOGIN_USER_LOGIN, null);
			password = sPref.getString(Configs.LOGIN_PASSWORD, null);
			new CheckUserCredentials().execute(username,password);
			pd.show();
			pd.setMessage("Restoring your previous login.");
		}
		
		
		System.out.println("onCreate");
		prefEdit = sPref.edit();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}

	public void createUser(View v) {
		startActivityForResult(new Intent(this, CreateUser.class),CreateUser.REQUEST_CODE);
	}

	public void login(View v) {
		username=username_box.getText().toString();
		try{
			password=Algorithms.computeSHA1sum(password_box.getText().toString());
		}catch(Exception e){};
		
		checkCredentials(username, password);
	}

	private void checkCredentials(String username, String password) {
		pd.show();
		pd.setMessage(getText(R.string.please_wait));
		if(username.trim().length()>0&&password.trim().length()>0){
			new CheckUserCredentials().execute(username, password);
		}else{
			Toast.makeText(context, "Error. Please check your credentials", Toast.LENGTH_LONG).show();
		}
	}
	
	public static boolean isLoggedIn(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		System.out.println("isLoggedin");
		return sPref.getString(Configs.LOGIN_USER_TOKEN, null)!=null;
	}
	
	public static String getToken(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		return sPref.getString(Configs.LOGIN_USER_TOKEN, null);
	}
	
	
	static String getUserId(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		return sPref.getString(Configs.LOGIN_USER_ID, null);
	}
	
	public static String getUserLogin(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		return sPref.getString(Configs.LOGIN_USER_LOGIN, null);
	}
	
	public static String getUserName(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		return sPref.getString(Configs.LOGIN_USER_USERNAME, null);
	}
	
	public static String getPassword(Context context){
		sPref = context.getSharedPreferences("aptoide_prefs", 0);
		return sPref.getString(Configs.LOGIN_PASSWORD, null);
	}
	

	class CheckUserCredentials extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			URL url;
			StringBuilder sb = null;
			String data = null;
			try {
				url = new URL("http://webservices.aptoide.com/webservices/checkUserCredentials");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
			    data += "&" + URLEncoder.encode("passhash", "UTF-8") + "=" + URLEncoder.encode(params[1],"UTF-8");
			    data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8");
			    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			    wr.write(data);
			    wr.flush();
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                wr.close();
                br.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			if(sb!=null){
				return sb.toString();
			}else{
				return "ERROR";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
			JSONObject array;
			try{
				array = new JSONObject(result);
				if(array.getString("status").equals("OK")){
					succeed=true;
					prefEdit.putString(Configs.LOGIN_PASSWORD, password);
					prefEdit.putString(Configs.LOGIN_USER_LOGIN, username);
					prefEdit.putString(Configs.LOGIN_USER_ID, Algorithms.computeSHA1sum(username));
					prefEdit.putString(Configs.LOGIN_USER_TOKEN, array.getString("token"));
					prefEdit.remove(Configs.LOGIN_USER_USERNAME);
					prefEdit.commit();
					Intent i = new Intent("login");
					sendBroadcast(i);
					finish();
				}else{
					Toast.makeText(Login.this, array.getString("errors"), Toast.LENGTH_LONG).show();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	@Override
	public void finish() {
		
		if(succeed){
			Intent i = new Intent();
			i.putExtra("username", username_box.getText().toString());
			setResult(RESULT_OK,i);
		}else{
			setResult(RESULT_CANCELED);
		}
		super.finish();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CreateUser.REQUEST_CODE:
			switch (resultCode) {
			case RESULT_OK:
				username_box.setText(data.getStringExtra("username"));
				password_box.setText(data.getStringExtra("password"));
				((Button)findViewById(R.id.login)).performClick();
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}
	
	public static void updateName(String name) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, ClientProtocolException, IOException, JSONException {
		String passwordSha1 = sPref.getString(Configs.LOGIN_PASSWORD, null);
		String email = sPref.getString(Configs.LOGIN_USER_LOGIN, null);
		String hmac = Algorithms.computeHmacSha1(email+passwordSha1+name+1, "bazaar_hmac");
		HttpPost post = new HttpPost("http://webservices.aptoide.com/webservices/createUser");
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
			
		}else{
			Editor sPrefeditor = sPref.edit();
			sPrefeditor.putString(Configs.LOGIN_USER_USERNAME, name);
			sPrefeditor.commit();
		}
		
		
	}

}
