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
