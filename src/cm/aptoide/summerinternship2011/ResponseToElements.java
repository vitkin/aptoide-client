/**
 * 
 */
package cm.aptoide.summerinternship2011;

/**
 * 
 * @author rafael
 *
 */
public enum ResponseToElements {
	
	RESPONSE, STATUS, ERRORS, ENTRY;
	
	public static ResponseToElements valueOfToUpper(String name) {
		return ResponseToElements.valueOf(name.toUpperCase());
	}
	
}
