package rainbowpc.controller.messages;

/**
 *
 * After a controller requests a partition to cache the scheduler responds with a partition
 */
public class CacheRequestResponse extends ControllerMessage{
	public static final String LABEL = "cacheRequestResponse";
	
	private int stringLength;
	private long startBlockNumber;
	private long endBlockNumber;
	private String hashMethod;

	public CacheRequestResponse(int stringLength, long startBlockNumber, long endBlockNumber, String hashMethod) {
		super(LABEL);
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
