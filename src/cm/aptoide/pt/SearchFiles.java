package cm.aptoide.pt;

import java.io.File;
import java.util.Vector;

import android.content.Context;

public class SearchFiles {

	private File dir = new File("/sdcard");
		
	private Vector<SdApkNode> apk_lst = new Vector<SdApkNode>();

	
	public SearchFiles(Context ctx){
		SearchAlg(dir);
	}
	
	
	private void SearchAlg(File fil){
		if (fil.isFile()) {
			SdApkNode node = new SdApkNode();
			node.path = fil.getAbsolutePath();
			node.name = fil.getName().substring(0, fil.getName().length()-4);
			apk_lst.add(node);
			return;
		}
		File[] next = fil.listFiles();
		SdApkNode node = null;
		for (int i=0; i < next.length; i++) {
			if ( next[i].isFile() && next[i].getName().endsWith(".apk")) {
				node = new SdApkNode();
				node.path = next[i].getAbsolutePath();
				node.name = next[i].getName().substring(0, next[i].getName().length()-4);
				apk_lst.add(node);
			}else if (next[i].isDirectory()) {
				SearchAlg(next[i]);
			}
		}
	}
	
	public void Search(){
		SearchAlg(dir);
	}
	
	public Vector<SdApkNode> getAll(){
		return apk_lst;
	}
	
	
	public void rescan() {
		apk_lst.clear();
		SearchAlg(dir);
	}
	
	public Vector<String> path_search (String dir, String up){
		Vector<String> return_vec = new Vector<String>();
		File tmp_file;
		
		if(dir == null && up == null){
			tmp_file = new File("/sdcard");
		}else{
			return_vec.add(up);
			tmp_file = new File(dir);
		}
		if(tmp_file.isFile() && tmp_file.getName().endsWith(".apk")){
			return_vec.add("isfile");
			return_vec.add(dir);
			return return_vec;	
		}else {
			File[] next = tmp_file.listFiles();
			for(int i = 0; i< next.length; i++)
				return_vec.add(next[i].getAbsolutePath());
			return return_vec;
		}
	}
}
