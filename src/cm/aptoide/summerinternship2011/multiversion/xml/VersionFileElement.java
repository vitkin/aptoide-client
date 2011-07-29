package cm.aptoide.summerinternship2011.multiversion.xml;

/**
 * 
 * @author rafael
 *
 */
public enum VersionFileElement {
	
	APP, PKG, VERSION, VERSIONNUMBER, URI, MD5;
	
	@Override
	public String toString() {
		
		return this.name().toLowerCase();
	}
	
	public static VersionFileElement valueOfToUpper(String str){
		return valueOf(str.toUpperCase());
	}
	
}
