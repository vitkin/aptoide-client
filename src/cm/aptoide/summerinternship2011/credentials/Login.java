/**
 * 
 */
package cm.aptoide.summerinternship2011.credentials;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cm.aptoide.pt.NetworkApis;
import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.ResponseToHandler;
import cm.aptoide.summerinternship2011.SetBlank;
import cm.aptoide.summerinternship2011.taste.TasteGetter;
import cm.aptoide.summerinternship2011.taste.UserTaste;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Implementation of the login interface.
 * 
 */
public class Login extends Dialog{
	
	private SharedPreferences sPref;
	private SharedPreferences.Editor prefEdit;
	
	private EditText username;
	private EditText password;
	private boolean isLoginSubmited;
	private boolean success;
	private ImageView like; 
	private ImageView dontlike;
	
	private String repo; 
	private String apkid; 
	private String apkversion;
	
	private UserTaste userTaste;
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public static enum InvoqueNature{ 
		CREDENTIALS_FAILED,
		NO_CREDENTIALS_SET,
		OVERRIDE_CREDENTIALS
	}
	
	/**
	 * Must define the parameters like, dontlike, repo, apkid && apkversion.
	 * 
	 * @param context
	 * @param nature
	 * 
	 * @param like
	 * @param dontlike
	 * @param repo
	 * @param apkid
	 * @param apkversion
	 * 
	 */
	public Login(Context context, InvoqueNature nature, ImageView like, 
				ImageView dontlike, String repo, String apkid, String apkversion, UserTaste userTaste) {
		super(context);
		sPref = context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		prefEdit = sPref.edit();
		isLoginSubmited = false;
		success = false;
		this.like = like;
		this.dontlike = dontlike;
		this.repo = repo;
		this.apkid = apkid;
		this.apkversion = apkversion;
		this.userTaste = userTaste;
	}

	/**
	 * 
	 * @param context
	 * @param nature
	 */
	public Login(Context context, InvoqueNature nature) {
		this(context, nature, null, null, null, null, null, null);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		this.setTitle(this.getContext().getString(R.string.setcredentials));
		
		username = ((EditText)findViewById(R.id.user));
		username.setOnFocusChangeListener(new SetBlank());
		password = ((EditText)findViewById(R.id.pass));
		
		password.setOnFocusChangeListener(new SetBlank());
		
		((Button)findViewById(R.id.passShowToogle)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				if(password.getTransformationMethod()!=null){
					password.setTransformationMethod(null);
					((Button)arg).setText(Login.this.getContext().getString(R.string.hidepass));
				}else{
					password.setTransformationMethod(PasswordTransformationMethod.getInstance());
					((Button)arg).setText(Login.this.getContext().getString(R.string.showpass));
				}
			}
		});
		
		((Button)findViewById(R.id.submitLogin)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				success = false;
				if(!((SetBlank)username.getOnFocusChangeListener()).getAlreadySetted()){
					Toast.makeText(Login.this.getContext(), Login.this.getContext().getString(R.string.usernotdef), Toast.LENGTH_LONG).show();
				}else if(!((SetBlank)password.getOnFocusChangeListener()).getAlreadySetted()){
					Toast.makeText(Login.this.getContext(), Login.this.getContext().getString(R.string.passwordnotdef), Toast.LENGTH_LONG).show();
				}else{
					try {
						final MessageDigest md = MessageDigest.getInstance("SHA");
						md.update(password.getText().toString().getBytes());
						
						ProgressDialog dialogProgress = ProgressDialog.show(getContext(), getContext().getString(R.string.top_please_wait),getContext().getString(R.string.loggingin),true);
						dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
						
						final AtomicBoolean successLogin = new AtomicBoolean(true);
						
						dialogProgress.setOnDismissListener(new OnDismissListener(){
							public void onDismiss(DialogInterface arg0) {
								if(successLogin.get()){
									
									if(success){
										Login.this.dismiss();
										isLoginSubmited = true;
									}
									
								}else{
									Toast.makeText(getContext(), Login.this.getContext().getString(R.string.unabletoexecute), Toast.LENGTH_LONG).show();
								}
							}
						});
						new LoginConfirmation(Login.this.getContext(), username.getText().toString(), ConfigsAndUtils.byteArrayToHexString(md.digest()), dialogProgress).execute();
					} catch (NoSuchAlgorithmException e) {
						//Toast.makeText(getContext(),  Login.this.getContext().getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
						Toast.makeText(getContext(),  Login.this.getContext().getString(R.string.unabletoexecute), Toast.LENGTH_LONG).show();
					}
					
				}
			}
		});
		
	}
	
	public boolean isLoginSubmited(){
		return isLoginSubmited;
	}
	
	/**
	 * @author rafael
	 * @since summerinternship2011
	 * 
	 */
	public class LoginConfirmation extends AsyncTask<Void, Void, ResponseToHandler>{
		
		private Context context;
		private String user;
		private String password;
		private ProgressDialog progress;
		private String useridLogin;
		private TasteGetter tasteGetter;
		
		public LoginConfirmation(Context context, String user, String password, ProgressDialog progress) throws NoSuchAlgorithmException {
			this.context = context;
			this.user = user;
			this.password = password;
			this.progress = progress;
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(this.user.getBytes());
			
			useridLogin = ConfigsAndUtils.byteArrayToHexString(md.digest());
			tasteGetter = null;
		}
		
		@Override
		protected ResponseToHandler doInBackground(Void... args) {
			
			try {
				
				ResponseToHandler response = checkCredentials(context, user, password);
				
				if(response.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK) && repo!=null && apkid!=null && apkversion!=null){
						
						tasteGetter = new  TasteGetter( repo, apkid, apkversion);
						tasteGetter.parse(context, useridLogin, this);
						
				}
				
				return response;
			} 
//			catch (IOException e) 					{}
//			catch (ParserConfigurationException e) 	{}
//			catch (SAXException e) 					{}
			catch(Exception e)						{}
			
			return null;
		}
		
		protected void onPostExecute(ResponseToHandler result) {
			if(result!=null){
				
				if(result.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
					
					
					prefEdit.putString("passwordLogin", password);
					prefEdit.putString("usernameLogin", user);
					
					prefEdit.putString("useridLogin", useridLogin);
					
					prefEdit.commit();
					success = true;
					
					if(tasteGetter!=null){
						if(tasteGetter.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
							switch(tasteGetter.getUserTaste()){
								case LIKE: 
									like.setImageResource(R.drawable.likehover);
									break;
								case DONTLIKE: 
									dontlike.setImageResource(R.drawable.dontlikehover);
									break;
								default: break;
							}
						}
					}
					
				}else{
					Toast.makeText(context, context.getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
				}
				
			}else{
				Toast.makeText(context, context.getString(R.string.unabletoexecutecheknet)+"bla", Toast.LENGTH_LONG).show();
			}
			
			progress.dismiss();
			
	    }
		
	}
	
	
	
	public static ResponseToHandler checkCredentials(Context context, String user, String password) throws IOException, ParserConfigurationException, SAXException{
		
		SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException 
		SAXParser sp = spf.newSAXParser();
		String url = String.format(ConfigsAndUtils.CHECK_CREDENTIALS_URL, URLEncoder.encode(user), URLEncoder.encode(password));
		
		InputStream stream = NetworkApis.getInputStream(context, url);
		BufferedInputStream bstream = new BufferedInputStream(stream);
		
		ResponseToHandler tasteResponseReader = new ResponseToHandler();
		
		sp.parse(new InputSource(bstream), tasteResponseReader);
		
		stream.close();
		bstream.close();
		
		return tasteResponseReader;  
		
	}

	public UserTaste getUserTaste() {
		return userTaste;
	}
	
}
