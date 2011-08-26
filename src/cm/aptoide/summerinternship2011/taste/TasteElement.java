package cm.aptoide.summerinternship2011.taste;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum TasteElement {
	
	RESPONSE, STATUS, LIKES, DISLIKES, ENTRY, USERNAME, TIMESTAMP, ERRORS;
	
	public static TasteElement valueOfToUpper(String name) {
		return TasteElement.valueOf(name.toUpperCase());
	}
	
}
