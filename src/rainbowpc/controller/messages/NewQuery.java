
package rainbowpc.controller.messages;

/**
 *
 * Tells controllers that a new query has been submitted and that this should be
 * searched for
 */
public class NewQuery extends ControllerMessage{
	public static final String LABEL = "newQuery";
	
	/*
	 * Hex encoded for maximum cross platform/language compatability and also 
	 * human readableness
	 */	
	private String query;
	// SHA1 or MD5
	private String hashMethod;
	public NewQuery(String query, String hashMethod) {
		super(LABEL);
		this.query = query;
		this.hashMethod = hashMethod;
	}

	public String getHashMethod() {
		return hashMethod;
	}

	public String getQuery() {
		return query;
	}
}
