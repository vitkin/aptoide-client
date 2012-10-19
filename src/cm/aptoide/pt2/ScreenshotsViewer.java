package cm.aptoide.pt2;

import java.util.ArrayList;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import cm.aptoide.pt2.adapters.ViewPagerAdapterScreenshots;
import com.viewpagerindicator.CirclePageIndicator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class ScreenshotsViewer extends FragmentActivity {
	
	String url;
	int position;
	private String[] images = new String[0];
	Context context;
	private String hashCode;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.screenshotsviewer);
		context = this;
		final ViewPager screenshots = (ViewPager) findViewById(R.id.screenShotsPager);
		final CirclePageIndicator pi = (CirclePageIndicator) findViewById(R.id.indicator);
		pi.setCentered(true);
		pi.setSnap(true);
		pi.setRadius(7.5f);
		pi.setFillColor(Color.BLACK);
		
		new Thread(new Runnable() {
			
			ArrayList<String> uri;
			public void run() {
				try{
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
					uri = getIntent().getStringArrayListExtra("url");
					hashCode = getIntent().getStringExtra("hashCode");
					images = uri.toArray(images);
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					runOnUiThread(new Runnable() {
						public void run() {
							if(images!=null&&images.length>0){
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,images,uri,hashCode));
								pi.setViewPager(screenshots);
								screenshots.setCurrentItem(getIntent().getIntExtra("position", 0));
							}
							
						}
					});
				}
			}
		}).start();
	}

}
