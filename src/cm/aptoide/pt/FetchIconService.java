package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;



public class FetchIconService extends  IntentService{



	public FetchIconService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String[] icon_uris = intent.getStringArrayExtra("icons");
		String file = intent.getStringExtra("file");
		String name = intent.getStringExtra("name");
		String usern = intent.getStringExtra("usern");
		String passwd = intent.getStringExtra("passwd");

		for (String icon : icon_uris) {
			try {
				String test_file = this.getString(R.string.icons_path) + name;
				File exists = new File(test_file);
				if(exists.exists())
					continue;
				
				Log.d("Aptoide","getIcon: " + icon);
				FileOutputStream saveit = new FileOutputStream(file);

				HttpResponse mHttpResponse = NetworkApis.getHttpResponse(icon, usern, passwd, this);

				if(mHttpResponse.getStatusLine().getStatusCode() == 401){
					return;
				}else if(mHttpResponse.getStatusLine().getStatusCode() == 403){
					return;
				}else{
					byte[] buffer = EntityUtils.toByteArray(mHttpResponse.getEntity());
					saveit.write(buffer);
				}

				Log.d("Aptoide","getIcon done: " + icon);
			}catch (Exception e) {
				//continue;
			}

		}

	}
}

