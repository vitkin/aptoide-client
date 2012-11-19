/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2.webservices.comments;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import cm.aptoide.pt2.R;

public class ViewComments extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allcomments);
		Comments comments = new Comments(this, getIntent().getStringExtra("webservicespath"));
		comments.getComments(getIntent().getStringExtra("repo"), getIntent().getStringExtra("apkid"), getIntent().getStringExtra("vername"), (LinearLayout) findViewById(R.id.container),true);
	}
}
