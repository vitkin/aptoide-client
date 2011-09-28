package cm.aptoide.pt.taste;

/**
 * @author rafael
 * @since 2.5.3
 * 
 */
public enum EnumResponseTasteElement {
	
	RESPONSE, STATUS, LIKES, DISLIKES, ENTRY, USERNAME, USERIDHASH, TIMESTAMP, ERRORS;
	
	public static EnumResponseTasteElement valueOfToUpper(String name) {
		return EnumResponseTasteElement.valueOf(name.toUpperCase());
	}
	
}
