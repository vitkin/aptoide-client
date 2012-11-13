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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cm.aptoide.pt2.Configs;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.views.ViewIconDownloadPermissions;

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
		
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream(), 8 * 1024);
		
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
			Log.d("Aptoide-NetworkUtils-checkServerConnection", "Checking on: "+client.getURL().toString());
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
	
	public static String  getUserAgentString(Context mctx){
		SharedPreferences sPref = mctx.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		String myid = sPref.getString("myId", "NoInfo");
		String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);
		return "aptoide-" + mctx.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_LOGIN, "");
	}
	
	
	public static boolean isConnectionAvailable(Context context){
		ConnectivityManager connectivityState = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean connectionAvailable = false;
		try {
			connectionAvailable = connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable mobile: "+connectionAvailable);	
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wifi: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wimax: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable ethernet: "+connectionAvailable);
		} catch (Exception e) { }
		
		return connectionAvailable;
	}
	
	public static boolean isPermittedConnectionAvailable(Context context, ViewIconDownloadPermissions permissions){
		ConnectivityManager connectivityState = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean connectionAvailable = false;
		if(permissions.isWiFi()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wifi: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isWiMax()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wimax: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isMobile()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable mobile: "+connectionAvailable);
			} catch (Exception e) { }
		}
		if(permissions.isEthernet()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable ethernet: "+connectionAvailable);
			} catch (Exception e) { }
		}

		Log.d("ManagerDownloads", "isPermittedConnectionAvailable: "+connectionAvailable+"  permissions: "+permissions);
		return connectionAvailable;
	}
	
}
