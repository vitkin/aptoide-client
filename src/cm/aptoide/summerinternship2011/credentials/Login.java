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
import android.util.Log;
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
	public static Boolean showing; 
	
	public static enum InvoqueNature{CREDENTIALS_FAILED, NO_CREDENTIALS_SET}
	
	
	public Login(Context context, InvoqueNature nature) {
		super(context);
		sPref = context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		prefEdit = sPref.edit();
		showing = false;
		Log.d("Dialog", "Constructor dialog");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.setTitle("Login");
		((EditText)findViewById(R.id.user)).setOnFocusChangeListener(new SetBlank());
		final EditText editText =((EditText)findViewById(R.id.pass));
		editText.setOnFocusChangeListener(new SetBlank());
		((Button)findViewById(R.id.clearLoginData)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				((EditText)findViewById(R.id.user)).setText("");
				((EditText)findViewById(R.id.pass)).setText("");
			}
		});
		((Button)findViewById(R.id.passShowToogle)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				if(editText.getTransformationMethod()!=null){
					editText.setTransformationMethod(null);
					((Button)arg).setText(Login.this.getContext().getString(R.string.hidepass));
				}else{
					editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
					((Button)arg).setText(Login.this.getContext().getString(R.string.showpass));
				}
			}
		});
		
		((Button)findViewById(R.id.submitLogin)).setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg) {
				
				try {
					MessageDigest md = MessageDigest.getInstance("SHA");
					md.update(editText.getText().toString().getBytes());
					prefEdit.putString("passwordLogin", ConfigsAndUtils.byteArrayToHexString(md.digest()));
					prefEdit.putString("usernameLogin", ((EditText)Login.this.findViewById(R.id.user)).getText().toString());
					prefEdit.commit();
				} catch (NoSuchAlgorithmException e) {
					Toast.makeText(getContext(),  Login.this.getContext().getString(R.string.failedcredentials), Toast.LENGTH_LONG).show();
				}
		      	Login.this.dismiss();
			}
		});
		
	}

}
