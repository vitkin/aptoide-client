package cm.aptoide.pt;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;

public class SetAptoideTheme {
	
	
	public static void setAptoideTheme(Context activity) {

		
		String theme_string = "APTOIDE_THEME_" + ApplicationAptoide.APTOIDETHEME.toUpperCase(Locale.ENGLISH);
		
		
		try{
			EnumAptoideThemes theme = EnumAptoideThemes.valueOf(theme_string);
			switch(theme){
			case APTOIDE_THEME_DEFAULT:
				activity.setTheme(R.style.Aptoide_Theme);
				break;
			case APTOIDE_THEME_MIDNIGHT:
				activity.setTheme(R.style.Aptoide_Theme_Midnight);
				break;
			case APTOIDE_THEME_MAGALHAES:
				activity.setTheme(R.style.Aptoide_Theme_Magalhaes);
				break;
			case APTOIDE_THEME_MAROON:
				activity.setTheme(R.style.Aptoide_Theme_Maroon);
				break;
			case APTOIDE_THEME_GOLD:
				activity.setTheme(R.style.Aptoide_Theme_Gold);
				break;
			case APTOIDE_THEME_ORANGE:
				activity.setTheme(R.style.Aptoide_Theme_Orange);
				break;
			case APTOIDE_THEME_SPRINGGREEN:
				activity.setTheme(R.style.Aptoide_Theme_SpringGreen);
				break;
			case APTOIDE_THEME_LIGHTSKY:
				activity.setTheme(R.style.Aptoide_Theme_LightSky);
				break;
			case APTOIDE_THEME_PINK:
				activity.setTheme(R.style.Aptoide_Theme_Pink);
				break;
			default:
				activity.setTheme(R.style.Aptoide_Theme);
				break;
		}
		}catch (Exception e) {
			activity.setTheme(R.style.Aptoide_Theme);
		}
		
		
	}
	

}
