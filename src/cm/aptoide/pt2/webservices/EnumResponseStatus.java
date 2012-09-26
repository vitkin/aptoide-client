/**
 * 
 */
package cm.aptoide.pt2.webservices;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * Defines the return status of a requested web service.
 */
public enum EnumResponseStatus {
	
	OK, FAIL;
	
	public static EnumResponseStatus valueOfToUpper(String name) {
		return EnumResponseStatus.valueOf(name.toUpperCase());
	}
	
}
