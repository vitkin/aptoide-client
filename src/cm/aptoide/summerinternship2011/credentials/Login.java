/**
 * 
 */
package cm.aptoide.summerinternship2011.credentials;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.ConfigsAndUtils;
import cm.aptoide.summerinternship2011.SetBlank;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author rafael
 *
 */
public class Login extends Dialog{
	
	private SharedPreferences sPref;
	private SharedPreferences.Editor prefEdit;
	
	private EditText username;
	private EditText password;
	private boolean isLoginSubmited;
	
	/**
	 * 
	 * @author rafael
	 *
	 */
	public static enum InvoqueNature{ 
		CREDENTIALS_FAILED,
		NO_CREDENTIALS_SET,
		OVERRIDE_CREDENTIALS
	}
	
	/**
	 * 
	 * @param context
	 * @param nature
	 */
	public Login(Context context, InvoqueNature nature) {
		super(context);
		sPref = context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		prefEdit = sPref.edit();
		isLoginSubmited = false;
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
				if(!((SetBlank)username.getOnFocusChangeListener()).getAlreadySetted()){
					Toast.makeText(Login.this.getContext(), Login.this.getContext().getString(R.string.usernotdef), Toast.LENGTH_LONG);
				}else if(!((SetBlank)password.getOnFocusChangeListener()).getAlreadySetted()){
					Toast.makeText(Login.this.getContext(), Login.this.getContext().getString(R.string.passwordnotdef), Toast.LENGTH_LONG);
				}else{
					try {
						MessageDigest md = MessageDigest.getInstance("SHA");
						md.update(password.getText().toString().getBytes());
						prefEdit.putString("passwordLogin", ConfigsAndUtils.byteArrayToHexString(md.digest()));
						prefEdit.putString("usernameLogin", username.getText().toString());
						prefEdit.commit();
					} catch (NoSuchAlgorithmException e) {
						Toast.makeText(getContext(),  Login.this.getContext().getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
					}
			      	Login.this.dismiss();
			      	isLoginSubmited =true;
				}
			}
		});
		
	}
	
	public boolean isLoginSubmited(){
		return isLoginSubmited;
	}
	
}
