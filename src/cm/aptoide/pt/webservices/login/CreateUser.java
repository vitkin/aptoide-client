package cm.aptoide.pt.webservices.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import cm.aptoide.pt.R;
import cm.aptoide.pt.webservices.comments.Algorithms;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUser extends Activity {
	
	EditText username_box;
	EditText password_box;
	String username;
	String password;
	
	public static final int REQUEST_CODE = 20;
	public static final String WEB_SERVICE_CREATEUSER = "http://webservices.bazaarandroid.com/webservices/createUser";
	
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user);
		context = this;
		username_box = (EditText) findViewById(R.id.email_box);
		password_box = (EditText) findViewById(R.id.password_box);
	}
	
	public void signUp(View v){
		username = username_box.getText().toString();
		password = password_box.getText().toString();
		new CreateUserTask().execute(username,password);
	}
	
	@Override
	public void finish() {
		
		Intent i = new Intent();
		i.putExtra("username", username_box.getText().toString());
		i.putExtra("password", password_box.getText().toString());
		setResult(RESULT_OK,i);
		super.finish();
	}
	
	public enum EnumCreateUserResponse{
		OK,FAIL
	}
	
	public class CreateUserTask extends AsyncTask<String, Void, JSONObject>{
		ProgressDialog pd = new ProgressDialog(context);
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
			pd.setMessage("Please wait.");
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject status;
			String data = null;
			StringBuilder sb = null;
			try{
				HttpURLConnection connection = (HttpURLConnection) new URL(WEB_SERVICE_CREATEUSER).openConnection();
				String password_sha1 = Algorithms.computeSHA1sum(params[1]);
				String hmac = Algorithms.computeHmacSha1(params[0]+password_sha1, "bazaar_hmac");
				
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
			    data += "&" + URLEncoder.encode("passhash", "UTF-8") + "=" + URLEncoder.encode(password_sha1,"UTF-8");
			    data += "&" + URLEncoder.encode("hmac", "UTF-8") + "=" + URLEncoder.encode(hmac,"UTF-8");
			    data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("json","UTF-8");
				
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
                status = new JSONObject(sb.toString());
                
			}catch(Exception e){
				return null;
			}
			
			
			return status;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			super.onPostExecute(json);
			
			EnumCreateUserResponse result = EnumCreateUserResponse.FAIL;
			try{
				result = EnumCreateUserResponse.valueOf(json.getString("status").toUpperCase());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			switch (result) {
			case OK:
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						pd.dismiss();
						finish();
					}
				}, 3000);
				break;
				
			case FAIL:
				try{
					Toast.makeText(context, json.getString("errors"), Toast.LENGTH_LONG).show();
				}catch (Exception e) {
					Toast.makeText(context, "Unkown Error. Try again.", Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				break;
			default:
				break;
			}
			
		}
		
	}
}

