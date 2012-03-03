package rainbowpc.scheduler.messages;

/**
 *
 * Sends a request to the scheduler to ask for a partition that it can begin to 
 * cache
 */
public class CacheRequest extends SchedulerMessage{
	public static String LABEL = "cacheRequest";
	private int cacheSize;
	public CacheRequest(String id, int cacheSize) {
		super(LABEL, id);
		this.cacheSize = cacheSize;
	}

	public int getCacheSize() {
		return cacheSize;
	}
	
	
}
