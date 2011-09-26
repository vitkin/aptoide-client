/**
 * 
 */
package cm.aptoide.pt.taste;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum EnumUserTaste { 

	LIKE, DONTLIKE, TASTELESS, NOTEVALUATED; 
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
