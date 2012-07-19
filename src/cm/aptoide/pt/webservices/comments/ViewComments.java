package cm.aptoide.pt.webservices.comments;

import cm.aptoide.pt.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ViewComments extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allcomments);
		Comments comments = new Comments(this, getIntent().getStringExtra("webservicespath"));
		comments.getComments(getIntent().getStringExtra("repo"), getIntent().getStringExtra("apkid"), getIntent().getStringExtra("vername"), (LinearLayout) findViewById(R.id.container),true);
	}
}
