package rainbowpc.scheduler.messages;

import rainbowpc.controller.messages.NewQuery;

/**
 *
 * Controller reports to the scheduler that a match for a query has been found
 */
public class QueryFound extends SchedulerMessage{
	public static final String LABEL = "queryFound";

	/*
	 * Hex encoded for maximum cross platform/language compatability and also 
	 * human readableness
	 */	
	private String query;
	// SHA1 or MD5
	private String hashMethod;
	private String plaintext;
	private int queryID;
	public QueryFound(String id, NewQuery query, String plaintext) {
		super(LABEL, id);
		this.query = query.getQuery();
		this.queryID = query.getQueryID();
		this.hashMethod = query.getHashMethod();
		this.plaintext = plaintext;
	}

	public String getHashMethod() {
		return hashMethod;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public String getQuery() {
		return query;
	}

	public int getQueryID() {
		return queryID;
	}
	
}
