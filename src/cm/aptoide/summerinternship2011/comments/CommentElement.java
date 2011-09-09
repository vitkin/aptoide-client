package cm.aptoide.summerinternship2011.comments;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 */
public enum CommentElement {
	
	ID, USERNAME, ANSWERTO, SUBJECT, TEXT, TIMESTAMP, USERIDHASH, LANG, RESPONSE, STATUS, LISTING, ENTRY, ERRORS;

	public static CommentElement valueOfToUpper(String name) {
		return CommentElement.valueOf(name.toUpperCase());
	}

}
