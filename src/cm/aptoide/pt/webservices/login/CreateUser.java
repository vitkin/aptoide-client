package cm.aptoide.pt.webservices.login;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.Configs;
import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.Security;
import cm.aptoide.pt.webservices.ResponseHandler;
import cm.aptoide.pt.webservices.taste.TasteGetter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
	private boolean success;
	ProgressDialog pd;
	private OnClickListener submitCreateUserListener = new OnClickListener() {
		
		
		

		public void onClick(View v) {
			
			
			password=((EditText) findViewById(R.id.password_box)).getText().toString();
			email=((EditText) findViewById(R.id.email_box)).getText().toString();
			
			pd = new ProgressDialog(context);
			pd.setMessage(getString(R.string.please_wait));
			pd.setCancelable(false);
			pd.show();
			new Thread(new Runnable() {
				
				public void run() {
					submit();
					
				}

				
			}).start();
			
	};};
	private boolean setIntent = false;
	private Context context;
	private SharedPreferences sPref;
	private SharedPreferences.Editor prefEdit;
	
	private void submit() {
		
		try{
			
			String sha1 = Algorithms.computeSHA1sum(password);
			String hmac;
			if(password.length()>0){
				hmac = Algorithms.computeHmacSha1(email+sha1, "bazaar_hmac");
			}else{
				return;
			}
			SharedPreferences sPref = getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
			String myid = sPref.getString("myId", "NoInfo");
			String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
			
			HttpPost post = new HttpPost("https://www.bazaarandroid.com/webservices/createUser");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			if(password.length()>0){
				nameValuePairs.add(new BasicNameValuePair("passhash", sha1));
			}
			
			nameValuePairs.add(new BasicNameValuePair("hmac", hmac));
			nameValuePairs.add(new BasicNameValuePair("mode", "json"));
			
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			BasicHttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			HttpClient client = new DefaultHttpClient(params);
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "aptoide-" + context.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid);
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
							stub = ((ViewStub) findViewById(R.id.error_stub)).inflate();
						}
						stub.setVisibility(View.VISIBLE);
						if(json.getString("status").equals("OK")){
							Toast.makeText(CreateUser.this, "Account successfuly created.", 1).show();
							setIntent=true;
							
							try {
								final MessageDigest md = MessageDigest.getInstance("SHA");
								md.update(password.getBytes());
								
								ProgressDialog dialogProgress = ProgressDialog.show(context, context.getString(R.string.please_wait),context.getString(R.string.loggingin),true);
								dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
								
								final AtomicBoolean successLogin = new AtomicBoolean(true);
								
								dialogProgress.setOnDismissListener(new OnDismissListener(){
									public void onDismiss(DialogInterface arg0) {
										if(successLogin.get()){
											
//											if(success){
//												isLoginSubmited = true;
//												LoginDialog.this.dismiss();
												
//												Log.d("Aptoide", "Login action broadcast sent");
//												Intent loginAction = new Intent();
//												loginAction.setAction("pt.caixamagica.aptoide.LOGIN_ACTION");
//												LoginDialog.this.getContext().sendBroadcast(loginAction);
												
//											}
											
										}else{
											Toast.makeText(context, context.getString(R.string.unabletoexecute), Toast.LENGTH_LONG).show();
										}
									}
								});
								new LoginConfirmation(context, email, Security.byteArrayToHexString(md.digest()), dialogProgress).execute();
							} catch (NoSuchAlgorithmException e) {
								//Toast.makeText(getContext(),  Login.this.getContext().getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
								Toast.makeText(context,  context.getString(R.string.unabletoexecute), Toast.LENGTH_LONG).show();
							}
							
						
							
						}
						((TextView)stub.findViewById(R.id.error)).setText(string =(json.has("errors")?json.getString("errors"):json.getString("status")));
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			pd.dismiss();
		}catch (Exception e) {
			e.printStackTrace();
			pd.dismiss();
			runOnUiThread(new Runnable() {
				
				public void run() {
					Toast.makeText(context,  context.getString(R.string.unabletoexecute), Toast.LENGTH_LONG).show();
					
				}
			});
			
		}
	}
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEdit = sPref.edit();
		setContentView(R.layout.createuser);
		findViewById(R.id.submitCreateUser).setOnClickListener(submitCreateUserListener );
		
		
	}
	
	@Override
	public void finish() {
		
		Intent i = new Intent();
		if(setIntent){
			
			i.putExtra("username", email);
			Toast.makeText(this, "Username "+email+" successfuly registered in www.bazaarandroid.com.", 1).show();
			
		}
		
		setResult(RESULT_OK, i);
		
		super.finish();
	
	}
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class LoginConfirmation extends AsyncTask<Void, Void, ResponseHandler>{
		
		private Context context;
		private String user;
		private String password;
		private ProgressDialog progress;
		private String useridLogin;
		private TasteGetter tasteGetter;
		
		
		public LoginConfirmation(Context context, String user, String password, ProgressDialog progress) throws NoSuchAlgorithmException {
			this.context = context;
			this.user = user.toLowerCase();
			this.password = password;
			this.progress = progress;
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(this.user.getBytes());
			
			useridLogin = Security.byteArrayToHexString(md.digest());
			tasteGetter = null;
		}
		
		@Override
		protected ResponseHandler doInBackground(Void... args) {
			
			try {
				
				ResponseHandler response = checkCredentials(context, user, password);
				
//				if(response.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK) && repo!=null && apkid!=null && apkversion!=null){
//						
//						tasteGetter = new  TasteGetter( repo, apkid, apkversion);
//						tasteGetter.parse(context, useridLogin, this);
//						
//				}
				if(!response.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK)){
					Thread.sleep(3000);
					response = checkCredentials(context, user, password);
				}
				
				return response;
			} 
			catch (IOException e) 					{
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) 	{
				e.printStackTrace();
			}
			catch (SAXException e) 					{
				e.printStackTrace();
			}
			catch(Exception e)						{
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(ResponseHandler result) {
			if(result!=null){
				
				if(result.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK)){
					
					
					prefEdit.putString(Configs.LOGIN_PASSWORD, password);
					prefEdit.putString(Configs.LOGIN_USER_NAME, user);
					prefEdit.putString(Configs.LOGIN_USER_ID, useridLogin);
					
					prefEdit.commit();
					success = true;
					finish();
//					if(tasteGetter!=null){
//						if(tasteGetter.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK)){
//							switch(tasteGetter.getUserTaste()){
//								case LIKE: 
//									like.setImageResource(R.drawable.likehover);
//									break;
//								case DONTLIKE: 
//									dontlike.setImageResource(R.drawable.dontlikehover);
//									break;
//								default: break;
//							}
//						}
//						synchronized(userTasteGetted){
//							userTasteGetted.setValue(tasteGetter.getUserTaste());
//						}
//					}
					
				}else{
					Toast.makeText(context, context.getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
				}
				
			}else{
				Toast.makeText(context, context.getString(R.string.unabletoexecutecheknet), Toast.LENGTH_LONG).show();
			}
			
			progress.dismiss();
			
			
	    }
		
	}
	
	
	
	public static ResponseHandler checkCredentials(Context context, String user, String password) throws IOException, ParserConfigurationException, SAXException{
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException 
		SAXParser sp = spf.newSAXParser();
		String url = String.format(Configs.WEB_SERVICE_GET_CHECK_CREDENTIALS, URLEncoder.encode(user), URLEncoder.encode(password));
		
		InputStream stream = NetworkApis.getInputStream(context, url);
		BufferedInputStream bstream = new BufferedInputStream(stream);
		
		ResponseHandler tasteResponseReader = new ResponseHandler();
		
		sp.parse(new InputSource(bstream), tasteResponseReader);
		
		stream.close();
		bstream.close();
		
		return tasteResponseReader;  
		
	}

}
