package cm.aptoide.pt2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.aptoide.pt2.util.NetworkUtils;
import cm.aptoide.pt2.util.RepoUtils;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

public class LatestLikesComments {

	private String repoName;
	private String endPointComments = "https://www.aptoide.com/webservices/listRepositoryComments/%s/json";
	private String endPointLikes = "https://www.aptoide.com/webservices/listRepositoryLikes/%s/json";
	private Context context;
	
	public LatestLikesComments(long store_id, Database db, Context context) {
		this.context=context;
		this.repoName = RepoUtils.split(db.getServer(store_id,false).url);
		
	}

	public Cursor getComments() {
		endPointComments = String.format(endPointComments, repoName);
		MatrixCursor cursor = new MatrixCursor(new String[]{"_id","apkid","name","text","username"});
		
		try {

			JSONObject respJSON = NetworkUtils.getJsonObject(new URL(endPointComments),context);
			JSONArray array = respJSON.getJSONArray("listing");
			
			for(int i = 0;i!=array.length();i++){
				String apkid = ((JSONObject)array.get(i)).getString("apkid");
				String name = ((JSONObject)array.get(i)).getString("name");
				String text = ((JSONObject)array.get(i)).getString("text");
				String username = ((JSONObject)array.get(i)).getString("username");
				cursor.newRow().add(i).add(apkid).add(name).add(text).add(username);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		return cursor;
	}

	public Cursor getLikes() {
		endPointLikes = String.format(endPointLikes, repoName);
		MatrixCursor cursor = new MatrixCursor(new String[]{"_id","apkid","name","like","username"});
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(endPointLikes).openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();

			JSONObject respJSON = new JSONObject(sb.toString());
			JSONArray array = respJSON.getJSONArray("listing");
			
			for(int i = 0;i!=array.length();i++){
				
				String apkid = ((JSONObject)array.get(i)).getString("apkid");
				String name = ((JSONObject)array.get(i)).getString("name");
				String like = ((JSONObject)array.get(i)).getString("like");
				String username = ((JSONObject)array.get(i)).getString("username");
				cursor.newRow().add(i).add(apkid).add(name).add(like).add(username);
				
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return cursor;
	}

	
	
	
}
