
package rainbowpc.controller.messages;

/**
 *
 * Message to tell controllers to stop searching for this query
 * Identical fields to new query
 * Occurs when query is abandoned, space is exhausted or a match is found
 */
public class StopQuery extends ControllerMessage{
	public static final String LABEL = "stopQuery";
	
	/*
	 * Hex encoded for maximum cross platform/language compatability and also 
	 * human readableness
	 */	
	private String query;
	// SHA1 or MD5
	private String hashMethod;
	public StopQuery(String query, String hashMethod) {
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
