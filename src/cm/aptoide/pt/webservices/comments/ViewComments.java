/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.webservices.comments;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import cm.aptoide.com.actionbarsherlock.app.SherlockActivity;
import cm.aptoide.com.actionbarsherlock.view.MenuItem;
import cm.aptoide.pt.R;
import cm.aptoide.pt.SetAptoideTheme;

public class ViewComments extends Activity /*SherlockActivity */{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SetAptoideTheme.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list_all_comments);
//		getSupportActionBar().setIcon(R.drawable.brand_padding);
//		getSupportActionBar().setTitle("All comments");
//		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Comments comments = new Comments(this, getIntent().getStringExtra("webservicespath"));
		comments.getComments(getIntent().getStringExtra("repo"), getIntent().getStringExtra("apkid"), getIntent().getStringExtra("vername"), (LinearLayout) findViewById(R.id.container),true);
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
