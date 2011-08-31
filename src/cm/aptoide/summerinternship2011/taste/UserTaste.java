/**
 * 
 */
package cm.aptoide.summerinternship2011.taste;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum UserTaste { 

	LIKE, DONTLIKE, TASTELESS, NOTEVALUATED; 
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
