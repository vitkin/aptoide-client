package cm.aptoide.pt2.sharing;


import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt2.R;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;



public class DialogShareOnFacebook extends Dialog{

	private static final String APP_ID = "477114135645153";
	private static final String[] PERMISSIONS = new String[] {"read_friendlists"};

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "backups-facebook-credentials";

	private Facebook facebook;
	private String nameToPost, iconToPost;
	private TextView share_text;
	private ImageView icon_image;

	Activity activity;
	private SharedPreferences sharedPreferences;

	public DialogShareOnFacebook(Activity activity, String facebookShareName, String facebookShareIcon) {
		super(activity);
		this.activity=activity;
		this.nameToPost = facebookShareName;
		this.iconToPost = facebookShareIcon;
	}

	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		sharedPreferences = getContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);

		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width=WindowManager.LayoutParams.FILL_PARENT;
		params.height=WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);

		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setTitle(R.string.share);

		setContentView(R.layout.dialog_share_facebook);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.facebook_bt);

		share_text = (TextView) findViewById(R.id.share_description);
		share_text.setText("I installed An App for Android");

		icon_image = (ImageView) findViewById(R.id.share_image);
		Drawable drawable = loadImageFromURL(iconToPost);
		icon_image.setImageDrawable(drawable);

		((Button)findViewById(R.id.FacebookShareButton)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (! facebook.isSessionValid()) {
					loginAndPostToWall();
				}
				else {
					postToWall(nameToPost, iconToPost);
				}
			}
		});

		((Button)findViewById(R.id.FacebookShareNotButton)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

	}

	public void loginAndPostToWall(){
		facebook.authorize(activity, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	public void postToWall(String name, String icon){
		Bundle params = new Bundle();
		params.putString("caption", "Aptoide");
		params.putString("link", "http://www.aptoide.com/");
		params.putString("description", "I installed "+name+" for Android");
		params.putString("picture", icon);
		params.putString("name", name);
		
		facebook.dialog(activity, "feed", params, new UpdateStatusListener());

	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			if(nameToPost != null){
				postToWall(nameToPost, iconToPost);
			}
		}
		public void onFacebookError(FacebookError error) {
			showToast(getContext().getString(R.string.facebook_authentication_failed));
		}
		public void onError(DialogError error) {
			showToast(getContext().getString(R.string.facebook_authentication_failed));
		}
		public void onCancel() {
			showToast(getContext().getString(R.string.facebook_authentication_cancelled));
		}
	}

	public class UpdateStatusListener extends DialogBaseShareListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Toast toast = Toast.makeText(getContext(), DialogShareOnFacebook.this.getContext().getString(R.string.facebook_message_posted), Toast.LENGTH_SHORT);
				toast.show();
				dismiss();
			} else {
				Toast toast = Toast.makeText(getContext(), DialogShareOnFacebook.this.getContext().getString(R.string.facebook_failed_post), Toast.LENGTH_SHORT);
				toast.show();
				dismiss();
			}
		}

		@Override
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
			dismiss();
		}

		@Override
		public void onCancel() {
			Toast toast = Toast.makeText(getContext(), "Update status cancelled", Toast.LENGTH_SHORT);
			toast.show();
			dismiss();
		}

		@Override
		public void onError(DialogError e) {
			e.printStackTrace();
			dismiss();
		}
	}

	private Drawable loadImageFromURL(String url){
		try	{
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		}catch (Exception e) {
			System.out.println("Exc="+e);
			return null;
		}
	}

	private void showToast(String message){
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
	}
}
