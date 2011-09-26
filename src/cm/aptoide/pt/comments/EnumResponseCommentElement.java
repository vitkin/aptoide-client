package cm.aptoide.pt.comments;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum EnumResponseCommentElement {
	
	ID, USERNAME, ANSWERTO, SUBJECT, TEXT, TIMESTAMP, USERIDHASH, LANG, RESPONSE, STATUS, LISTING, ENTRY, ERRORS;

	public static EnumResponseCommentElement valueOfToUpper(String name) {
		return EnumResponseCommentElement.valueOf(name.toUpperCase());
	}

}
