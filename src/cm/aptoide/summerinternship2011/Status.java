/**
 * 
 */
package cm.aptoide.summerinternship2011;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 * Defines the return status of a requested web service.
 */
public enum Status {
	
	OK, FAIL;
	
	public static Status valueOfToUpper(String name) {
		return Status.valueOf(name.toUpperCase());
	}
	
}
