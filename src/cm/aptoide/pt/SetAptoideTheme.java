package cm.aptoide.pt;

import android.app.Activity;
import android.content.Context;

public class SetAptoideTheme {
	
	
	public static void setAptoideTheme(Context activity) {

		EnumAptoideThemes theme = EnumAptoideThemes.APTOIDE_THEME;
		
		switch(theme){
			case APTOIDE_THEME:
				activity.setTheme(R.style.Aptoide_Theme);
				break;
			case APTOIDE_THEME_MIDNIGHT:
				activity.setTheme(R.style.Aptoide_Theme_Midnight);
				break;
			default:
				activity.setTheme(R.style.Aptoide_Theme);
				break;
		}
		
	}
	

}
