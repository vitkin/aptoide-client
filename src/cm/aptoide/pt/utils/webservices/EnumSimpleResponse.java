/**
 * 
 */
package cm.aptoide.pt.utils.webservices;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum EnumSimpleResponse {
	
	RESPONSE, STATUS, ERRORS, ENTRY;
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static EnumSimpleResponse valueOfToUpper(String name) {
		EnumSimpleResponse[] array = EnumSimpleResponse.values();
		for(EnumSimpleResponse element: array){
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
