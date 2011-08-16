/**
 * 
 */
package comments;

/**
 * 
 * @author rafael
 * 
 * Defines the status of a requested web service.
 */
public enum Status {
	
	OK, FAILED;
	
	public static Status valueOfToUpper(String name) {
		return Status.valueOf(name.toUpperCase());
	}
	
}
