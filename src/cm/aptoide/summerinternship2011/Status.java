/**
 * 
 */
package cm.aptoide.summerinternship2011;

/**
 * 
 * @author rafael
 * 
 * Defines the status of a requested web service.
 */
public enum Status {
	
	OK, FAIL;
	
	public static Status valueOfToUpper(String name) {
		return Status.valueOf(name.toUpperCase());
	}
	
}
