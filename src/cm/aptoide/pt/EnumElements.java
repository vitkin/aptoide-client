package cm.aptoide.pt;

public enum EnumElements {
PACKAGE,NAME,APKID,UKNOWN_ELEMENT,  APPSCOUNT, BASEPATH, ICONSPATH, SCREENSPATH, WEBSERVICESPATH, APKPATH, PATH, VER, VERCODE, ICON, DATE, MD5H, DWN, RAT, CATG, CATG2, SZ, AGE, MINSDK, MINSCREEN, DELTA, REPOSITORY, APKLST, DEL;

public static EnumElements lookup(String string){
	try{
		return valueOf(string);
	}catch (Exception e) {
		return UKNOWN_ELEMENT;
	}
	
}
}
