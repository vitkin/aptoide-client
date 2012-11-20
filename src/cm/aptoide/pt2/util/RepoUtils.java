package cm.aptoide.pt2.util;

public class RepoUtils {

	public static String split(String repo){
		return repo.split("http://")[1].split(".store")[0];
	}
	
}
