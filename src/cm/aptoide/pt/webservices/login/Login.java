/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.webservices.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import cm.aptoide.pt.Configs;
import cm.aptoide.pt.Database;
import cm.aptoide.pt.R;
import cm.aptoide.pt.AptoideThemePicker;
import cm.aptoide.pt.util.Algorithms;
import cm.aptoide.pt.views.ViewApk;

public class Login extends Activity /*SherlockActivity */{
	ProgressDialog pd;
	EditText username_box;
	EditText password_box;
	String username;
	String password;
	TextView forgot_password;
	Button createUser;
	
	static Context context;
	private boolean succeed = false;
	private static SharedPreferences sPref;
	private static SharedPreferences.Editor prefEdit;
	public final static int REQUESTCODE = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		AptoideThemePicker.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		
		context = this;
//		getSupportActionBar().setIcon(R.drawable.brand_padding);
//		getSupportActionBar().setTitle(getString(R.string.my_account));
//		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		pd = new ProgressDialog(context);
		if (isLoggedIn(context)) {
			setContentView(R.layout.form_logout);
			((TextView) findViewById(R.id.username)).setText(getUserLogin(this));
		} else{
			setContentView(R.layout.form_login);
			username_box = (EditText) findViewById(R.id.username);
			password_box = (EditText) findViewById(R.id.password);
			
			createUser = (Button) findViewById(R.id.new_to_aptoide);
			SpannableString newUserString=new SpannableString(getString(R.string.new_to_aptoide));
			newUserString.setSpan(new UnderlineSpan(), 0, newUserString.length(), 0);
			createUser.setText(newUserString);
			
			forgot_password = (TextView) findViewById(R.id.forgot_password);
			SpannableString forgetString = new SpannableString(getString(R.string.forgot_passwd));
			forgetString.setSpan(new UnderlineSpan(), 0, forgetString.length(), 0);
			forgot_password.setText(forgetString);
			forgot_password.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent passwordRecovery = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.aptoide.com/account/password-recovery"));
					startActivity(passwordRecovery);
				}
			});
//			if (sPref.getString(Configs.LOGIN_USER_LOGIN, null) != null) {
//
//				username = sPref.getString(Configs.LOGIN_USER_LOGIN, null);
//				password = sPref.getString(Configs.LOGIN_PASSWORD, null);
//				new CheckUserCredentials().execute(username, password);
//				pd.show();
//				pd.setMessage("Restoring your previous login.");
//			}
		}
		prefEdit = sPref.edit();

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_main, menu);
	// return true;
	// }

	public void createUser(View v) {
		startActivityForResult(new Intent(this, CreateUser.class), CreateUser.REQUEST_CODE);
		Log.d("","click " + v.getId());
	}

	public void login(View v) {
		username = username_box.getText().toString();
		try {
			password = Algorithms.computeSHA1sum(password_box.getText()
					.toString());
		} catch (Exception e) {
		}

		checkCredentials(username, password);
	}

	public void logout(View v) {
		prefEdit.remove(Configs.LOGIN_PASSWORD);
		prefEdit.remove(Configs.LOGIN_USER_ID);
		prefEdit.remove(Configs.LOGIN_USER_LOGIN);
		prefEdit.remove(Configs.LOGIN_USER_TOKEN);
		prefEdit.remove(Configs.LOGIN_USER_USERNAME);
		prefEdit.commit();
		ViewApk apk = new ViewApk();
		apk.setApkid("recommended");
		Database.getInstance().deleteItemBasedApks(apk);
		Intent i = new Intent("login");
		sendBroadcast(i);
		onCreate(null);
	}

	private void checkCredentials(String username, String password) {
		if (username.trim().length() > 0 && password.trim().length() > 0) {
			new CheckUserCredentials().execute(username, password,"false");
		} else {
			Toast toast = Toast.makeText(context,
					context.getString(R.string.check_your_credentials),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 30);
			toast.show();
		}
	}

	public static boolean isLoggedIn(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		System.out.println("isLoggedin");
		return sPref.getString(Configs.LOGIN_USER_TOKEN, null) != null;
	}

	public static String getToken(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sPref.getString(Configs.LOGIN_USER_TOKEN, null);
	}

	static String getUserId(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sPref.getString(Configs.LOGIN_USER_ID, null);
	}

	public static String getUserLogin(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sPref.getString(Configs.LOGIN_USER_LOGIN, null);
	}

	public static String getUserName(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sPref.getString(Configs.LOGIN_USER_USERNAME, null);
	}

	public static String getPassword(Context context) {
		sPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sPref.getString(Configs.LOGIN_PASSWORD, null);
	}

	class CheckUserCredentials extends AsyncTask<String, Void, JSONObject> {

		int retry = 0;
		String username_string;
		String password_string;
		String fromSignIn;
		JSONObject array;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
			pd.setMessage(getText(R.string.please_wait));
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			fromSignIn = params[2];
			username_string = params[0];
			password_string = params[1];
			try {
				return checkUserCredentials(username_string,password_string);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * @throws MalformedURLException
		 * @throws IOException
		 * @throws UnsupportedEncodingException
		 * @throws JSONException
		 * @throws InterruptedException
		 */
		private JSONObject checkUserCredentials(String username, String password) throws MalformedURLException,
				IOException, UnsupportedEncodingException, JSONException, InterruptedException {
			URL url;
			StringBuilder sb;
			String data;
			url = new URL("http://webservices.aptoide.com/webservices/checkUserCredentials");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
			data += "&" + URLEncoder.encode("passhash", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
			data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8");
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			wr.close();
			br.close();
			JSONObject array = new JSONObject(sb.toString());
			System.out.println(array);
			if(array.has("errors")){
				if(array.getString("errors").contains("credentials")&&fromSignIn.equals("true")){
					if(retry>=10){
						array = null;
						throw new IOException();
					}
					Thread.sleep(3000);
					retry++;
					System.out.println("Retrying " +retry );
					array = checkUserCredentials(username,password);
				}
			}
			return array;
		}

		@Override
		protected void onPostExecute(JSONObject array) {
			super.onPostExecute(array);
			pd.dismiss();
			try {
				if (array.getString("status").equals("OK")) {
					succeed = true;
					prefEdit.putString(Configs.LOGIN_PASSWORD, password_string);
					prefEdit.putString(Configs.LOGIN_USER_LOGIN, username_string);
					prefEdit.putString(Configs.LOGIN_USER_ID,
							Algorithms.computeSHA1sum(username_string));
					prefEdit.putString(Configs.LOGIN_USER_TOKEN,
							array.getString("token"));
					prefEdit.remove(Configs.LOGIN_USER_USERNAME);
					prefEdit.commit();
					Intent i = new Intent("login");
					sendBroadcast(i);
					finish();
				} else {
					Toast toast = Toast.makeText(Login.this,
							array.getString("errors"), Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
							0, 30);
					toast.show();
				}
			} catch (Exception e) {
				Toast toast= Toast.makeText(context,
						context.getString(R.string.error_occured), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
						toast.show();
				e.printStackTrace();
			}

		}
	}

	@Override
	public void finish() {

		if (succeed) {
			Intent i = new Intent();
			i.putExtra("username", username_box.getText().toString());
			setResult(RESULT_OK, i);
		} else {
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
				if(!Login.isLoggedIn(context)){
					try {
						username_box.setText(data.getStringExtra("username"));
						password_box.setText(data.getStringExtra("password"));
						new CheckUserCredentials().execute(data.getStringExtra("username"), Algorithms.computeSHA1sum(data.getStringExtra("password")),"true");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	public static void updateName(String name) throws InvalidKeyException,
			IllegalStateException, NoSuchAlgorithmException,
			ClientProtocolException, IOException, JSONException {
		String passwordSha1 = sPref.getString(Configs.LOGIN_PASSWORD, null);
		String email = sPref.getString(Configs.LOGIN_USER_LOGIN, null);
		String hmac = Algorithms.computeHmacSha1(email + passwordSha1 + name
				+ 1, "bazaar_hmac");
		HttpPost post = new HttpPost(
				"http://webservices.aptoide.com/webservices/createUser");
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
		if (json.has("errors")) {

		} else {
			Editor sPrefeditor = sPref.edit();
			sPrefeditor.putString(Configs.LOGIN_USER_USERNAME, name);
			sPrefeditor.commit();
		}

	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

}
