
package rainbowpc.scheduler.messages;

/**
 *
 * If the controller is going away it should release its cache before disconnecting
 * Also if it lost its cache in some way it should also release it
 */
public class CacheRelease extends SchedulerMessage {
	public static String LABEL = "cacheRelease";
	
	private int stringLength;
	private long startBlockNumber;
	private long endBlockNumber;
	private String hashMethod;

	public CacheRelease(String id, int stringLength, long startBlockNumber, long endBlockNumber, String hashMethod) {
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
