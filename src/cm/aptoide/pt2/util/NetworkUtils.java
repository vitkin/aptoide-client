package cm.aptoide.pt2.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import cm.aptoide.pt2.Configs;
import cm.aptoide.pt2.R;

public class NetworkUtils {

	
	private static int TIME_OUT = 10000;
	
	
	public static BufferedInputStream getInputStream(URL url, String username, String password, Context mctx) throws IOException{
		URLConnection connection = url.openConnection();
		if(username!=null && password!=null){
			String basicAuth = "Basic " + new String(Base64.encode((username+":"+password).getBytes(),Base64.NO_WRAP ));
			connection.setRequestProperty ("Authorization", basicAuth);
		}
		SharedPreferences sPref = mctx.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		String myid = sPref.getString("myId", "NoInfo");
		String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
		connection.setRequestProperty("User-Agent", "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_LOGIN, ""));
		
		BufferedInputStream bis = new BufferedInputStream(
				connection.getInputStream(), 8 * 1024);
		
		
		Log.i("Aptoide", "Getting: "+url.toString());
		connection.setConnectTimeout(TIME_OUT);
		connection.setReadTimeout(TIME_OUT);
		return bis;
		
	}
	
	public static void setTimeout(int timeout){
		NetworkUtils.TIME_OUT=timeout;
	}
	
	public static int checkServerConnection(final String string, final String username, final String password) {
		try {

			HttpURLConnection client = (HttpURLConnection) new URL(string
					+ "info.xml").openConnection();
			if (username != null && password != null) {
				String basicAuth = "Basic "
						+ new String(Base64.encode(
								(username + ":" + password).getBytes(),
								Base64.NO_WRAP));
				client.setRequestProperty("Authorization", basicAuth);
			}
			client.setConnectTimeout(TIME_OUT);
			client.setReadTimeout(TIME_OUT);
			
			if (client.getContentType().equals("application/xml")) {
				return 0;
			} else {
				return client.getResponseCode();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static JSONObject getJsonObject(URL url, Context mctx) throws IOException, JSONException{
		String line = null;
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(getInputStream(url, null, null, mctx)));
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null){
			sb.append(line + '\n');
		}
			
		return new JSONObject(sb.toString());
		
	}
	
}
