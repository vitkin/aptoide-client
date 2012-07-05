package cm.aptoide.pt;

import android.content.Context;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Describes the languages supported by aptoide.
 * The code language follow the for language ISO 639-1 and the ISO 3166-1 for countries.
 */
public enum SupportedLanguages {
	
	en_GB, pt_PT, es_ES, fr_FR, de_DE, it_IT, zu_RU, zh_CN,	ko_KR;
	
	public static String getMyCountrCode(Context context){
		return context.getResources().getConfiguration().locale.getLanguage()+"_"+context.getResources().getConfiguration().locale.getCountry();
	}
}
