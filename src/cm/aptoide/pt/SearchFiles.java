/*
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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
	
	public void cleanAll(){
		File home = new File(dir+"/.aptoide");
		File[] allfiles = home.listFiles();
		for(File fl: allfiles){
			if(fl.getName().endsWith(".apk")){
				fl.delete();
			}
		}
	}
}
