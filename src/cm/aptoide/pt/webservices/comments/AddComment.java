/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.webservices.comments;

import java.net.URL;

import cm.aptoide.com.actionbarsherlock.app.SherlockActivity;
import cm.aptoide.com.actionbarsherlock.view.MenuItem;
import cm.aptoide.pt.webservices.login.Login;
import cm.aptoide.pt.R;
import cm.aptoide.pt.SetAptoideTheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddComment extends Activity /*SherlockActivity */{

	
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
		SetAptoideTheme.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.form_add_comment);
//		getSupportActionBar().setIcon(R.drawable.brand_padding);
//		getSupportActionBar().setTitle("Add comment");
//		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
