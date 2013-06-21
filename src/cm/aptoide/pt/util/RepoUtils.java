package cm.aptoide.pt.util;

import android.util.Log;

public class RepoUtils {

	public static String split(String repo){
		return repo.split("http://")[1].split(".store")[0].split(".bazaarandroid.com")[0];
	}
    public static String formatRepoUri(String uri_str) {
        if (uri_str.contains("http//")) {
            uri_str = uri_str.replaceFirst("http//", "http://");
        }

        if (uri_str.length() != 0 && uri_str.charAt(uri_str.length() - 1) != '/') {
            uri_str = uri_str + '/';
            Log.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
        }
        if (!uri_str.startsWith("http://")) {
            uri_str = "http://" + uri_str;
            Log.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
        }
        return uri_str;
    }

}
