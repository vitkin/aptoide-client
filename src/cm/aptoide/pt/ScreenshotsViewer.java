package cm.aptoide.pt;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ScreenshotsViewer extends FragmentActivity {
	
	String url;
	int position;
	private String[] images;
	Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.screenshotsviewer);
		context = this;
		final ViewPager screenshots = (ViewPager) findViewById(R.id.screenShotsPager);
		final CirclePageIndicator pi = (CirclePageIndicator) findViewById(R.id.indicator);
		pi.setCentered(true);
		pi.setFillColor(Color.BLACK);
		
		new Thread(new Runnable() {
			
			private JSONArray imagesurl;
			String uri;
			public void run() {
				try{
					HttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
					HttpResponse response=null;
					HttpGet request = new HttpGet();
					uri = getIntent().getStringExtra("url");
					request.setURI(new URI(uri));
					System.out.println(request.getURI());
					response = client.execute(request);
					System.out.println(request.getURI()+"");
					String temp = EntityUtils.toString(response.getEntity());

					JSONObject respJSON;
					respJSON = new JSONObject(temp);

					imagesurl = respJSON.getJSONArray("listing");
					images = new String[imagesurl.length()];
					for ( int i = 0; i!= imagesurl.length();i++){
						images[i]=imagesurl.getString(i);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					runOnUiThread(new Runnable() {
						public void run() {
							if(images!=null&&images.length>0){
								screenshots.setAdapter(new ViewPagerAdapterScreenshots(context,images,uri));
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
