package cm.aptoide.summerinternship2011.multiversion.xml;

/**
 * 
 * @author rafael
 *
 */
public enum VersionFileElement {
	
	APP, PKG, VERSION, VERSION_NUMBER, URI, MD5, NOT_VALID;
	
	@Override
	public String toString() {
		
		return this.name().toLowerCase();
	}
	
	public static VersionFileElement valueOfToUpper(String str){
		try{
			return valueOf(str.toUpperCase());
		}catch(IllegalArgumentException e){}
		return NOT_VALID;
	}
	
}
