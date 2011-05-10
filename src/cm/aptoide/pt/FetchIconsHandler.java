package cm.aptoide.pt;

import java.util.Vector;

import android.content.Context;

public class FetchIconsHandler {

	static Vector<Vector<String>> icon_lst = new Vector<Vector<String>>();
	
	public static void addIconList(Vector<String> dirt_icons, Context mctx){
		icon_lst.add(dirt_icons);
		
		
		//mctx.startService(service);
	}
	
}
