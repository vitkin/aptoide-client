package cm.aptoide.pt2.webservices.comments;

import java.net.URL;

import cm.aptoide.pt2.R;
import cm.aptoide.pt2.webservices.login.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddComment extends Activity {

	
	public static final int ADD_COMMENT_REQUESTCODE = 0;
	Context context;
	private String webservicespath;
	private String version;
	private String apkid;
	private String repo;
	private EditText comment_box;
	private EditText name_box;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcomment);
		context = this;
		this.repo = getIntent().getStringExtra("repo");
		this.apkid = getIntent().getStringExtra("apkid");
		this.version = getIntent().getStringExtra("version");
		this.webservicespath = getIntent().getStringExtra("webservicespath");
		comment_box = (EditText) findViewById(R.id.comment);
		name_box = (EditText) findViewById(R.id.name);
		if(Login.getUserName(context)!=null){
			name_box.setVisibility(View.GONE);
		}
		
		if(Login.isLoggedIn(context)){
			name_box.setText(Login.getUserLogin(context).split("@")[0]);
		}
		
	}
	
	public void postComment(View v){
		if(name_box.getText().length()>0){
			username = name_box.getText().toString();
		}else{
			username = null;
		}
		if(Login.isLoggedIn(context)){
			try{
//				String username = ((EditText) findViewById(R.id.name)).getText().toString();
				String comment = comment_box.getText().toString();
				Comments comments = new Comments(this, webservicespath);
				comments.postComment(repo,apkid,version,comment,username);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			Intent i = new Intent(this,Login.class);
			startActivityForResult(i, Login.REQUESTCODE);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case Login.REQUESTCODE:
			switch (resultCode) {
			case RESULT_OK:
				if(name_box.getText().toString().trim().length()==0){
					name_box.setText(data.getStringExtra("username").split("@")[0]);
				}
				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		System.out.println("onsaveinstancestate");
		outState.putString("comment", comment_box.getText().toString());
	}
}
