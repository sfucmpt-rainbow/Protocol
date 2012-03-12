package rainbowpc.node.messages;

//import rainbowpc.protocols.node.NodeMessage;

public class WorkMessage extends NodeMessage {
	public final static String LABEL = "workParcel";

	private String nodeName;
	private int partitionId;
	private int blockId;
	private String target;
	private long startIndex;
	private long endIndex;
	private int stringLength;	
	private boolean found;
	private String reversed;

	public WorkMessage(String nodeName, String target, int partitionId, int blockId, long startIndex, long endIndex, int stringLength) {
		super(LABEL);
		this.nodeName = nodeName;
		this.partitionId = partitionId;
		this.blockId = blockId;
		this.target = target;
		this.stringLength = stringLength;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getNodeName() {
		return nodeName;
	}

	public int getPartitionId() {
		return partitionId;
	}

	public int getBlockId() {
		return blockId;
	}

	public long getStartIndex() {
		return startIndex;
	}

	public long getEndIndex() {
		return endIndex;
	}

	public int getStringLength() {
		return stringLength;
	}

	public String getTarget() {
		return target;
	}

	public void markFound(String reversed) {
		found = true;
		this.reversed = reversed;
	}

	public void markUnfound() {
		found = false;
	}

	public boolean targetFound() {
		return found;
	}

	public String getReversed() {
		return reversed;
	}
}
