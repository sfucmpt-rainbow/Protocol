package rainbowpc.scheduler.messages;

/**
 *
 * Controller requests cache, scheduler sends response, controller completes cache
 * Then sends this message to inform the scheduler that the cache is ready
 * 
 * Also if controller has this stored somewhere already can just send this message
 * to the server to inform it that it is ready
 */
public class CacheReady extends SchedulerMessage {

	public static final String LABEL = "cacheReady";
	private int stringLength;
	private long startBlockNumber;
	private long endBlockNumber;
	private String hashMethod;

	public CacheReady(String id, int stringLength, long startBlockNumber, long endBlockNumber, String hashMethod) {
		super(LABEL, id);
		this.stringLength = stringLength;
		this.startBlockNumber = startBlockNumber;
		this.endBlockNumber = endBlockNumber;
		this.hashMethod = hashMethod;
	}

	public long getEndBlockNumber() {
		return endBlockNumber;
	}

	public long getStartBlockNumber() {
		return startBlockNumber;
	}

	public int getStringLength() {
		return stringLength;
	}

	public String getHashMethod() {
		return hashMethod;
	}
	
}
