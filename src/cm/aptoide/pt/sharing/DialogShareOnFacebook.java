/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.sharing;


import java.io.InputStream;
import java.net.URL;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import cm.aptoide.com.facebook.android.DialogError;
import cm.aptoide.com.facebook.android.Facebook;
import cm.aptoide.com.facebook.android.Facebook.DialogListener;
import cm.aptoide.com.facebook.android.FacebookError;
import cm.aptoide.pt.ApplicationAptoide;
import cm.aptoide.pt.AptoideThemePicker;
import cm.aptoide.pt.R;

import com.actionbarsherlock.view.Window;



public class DialogShareOnFacebook extends Dialog{

	private static final String APP_ID = "477114135645153";
	private static final String[] PERMISSIONS = new String[] {"read_friendlists"};

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "aptoide-facebook-credentials";

	private Facebook facebook;
	private String nameToPost, iconToPost, messageToPost, descriptionToPost, storeLinkToPost;
	private TextView share_description;
	private ImageView icon_image;
	private TextView store_name;
	private TextView share_visit;
	
	Activity activity;
	private SharedPreferences sharedPreferences;
	private TextView post_message;

	public DialogShareOnFacebook(Activity activity, String facebookShareName, String facebookShareIcon, String facebookShareText, String facebookShareDescription, String facebookShareStoreLink) {
		super(activity);
		this.activity=activity;
		this.nameToPost = facebookShareName;
		this.iconToPost = facebookShareIcon;
		this.messageToPost = facebookShareText;
		this.descriptionToPost = facebookShareDescription;
		this.storeLinkToPost = facebookShareStoreLink;
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
		AptoideThemePicker.setAptoideTheme(activity);
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

		share_description = (TextView) findViewById(R.id.share_description);
		share_description.setText(messageToPost);
		icon_image = (ImageView) findViewById(R.id.share_image);
		Drawable drawable = loadImageFromURL(iconToPost);
		icon_image.setImageDrawable(drawable);
		share_visit = (TextView) findViewById(R.id.share_visit);
		share_visit.setText(descriptionToPost);
		store_name = (TextView) findViewById(R.id.share_store);
		store_name.setText(storeLinkToPost);
		
		post_message = (TextView) findViewById(R.id.post_message);
		post_message.setText(getContext().getString(R.string.want_to_share, ApplicationAptoide.MARKETNAME));
		
		((Button)findViewById(R.id.FacebookShareButton)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (! facebook.isSessionValid()) {
					loginAndPostToWall();
				}
				else {
					postToWall(nameToPost, iconToPost, messageToPost, descriptionToPost, storeLinkToPost);
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

	public void postToWall(String name, String icon, String message, String description, String storeLink){
		Bundle params = new Bundle();
		params.putString("caption", message);
		params.putString("link", storeLink);
		params.putString("description", description + " - " + storeLink);
		params.putString("picture", icon);
		params.putString("name", name);
		
		facebook.dialog(activity, "feed", params, new UpdateStatusListener());

	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			if(nameToPost != null){
				postToWall(nameToPost, iconToPost, messageToPost, descriptionToPost, storeLinkToPost);
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
			Toast toast = Toast.makeText(getContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT);
			toast.show();
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
