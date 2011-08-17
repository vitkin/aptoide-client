package comments;

/**
 * 
 * @author rafael
 *
 */
public enum CommentElement {
	
	RESPONSE, STATUS, LISTING, ENTRY , ID, USERNAME, ANSWERTO, SUBJECT, TEXT, TIMESTAMP;

	public static CommentElement valueOfToUpper(String name) {
		return CommentElement.valueOf(name.toUpperCase());
	}

}
