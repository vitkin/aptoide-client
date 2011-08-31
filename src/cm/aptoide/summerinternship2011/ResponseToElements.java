/**
 * 
 */
package cm.aptoide.summerinternship2011;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum ResponseToElements {
	
	RESPONSE, STATUS, ERRORS, ENTRY;
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static ResponseToElements valueOfToUpper(String name) {
		ResponseToElements[] array = ResponseToElements.values();
		for(ResponseToElements element: array){
			if(element.toString().equals(name))
				return element;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
