package rainbowpc.controller.messages;

public class WorkBlockSetup extends ControllerMessage {
	public static final String LABEL = "workBlockSetup";
	private int stringLength;
	private long startBlockNumber;
	private long endBlockNumber;

	public WorkBlockSetup(int stringLength, long startBlockNumber, long endBlockNumber) {
		super(LABEL);
		this.stringLength = stringLength;
		this.startBlockNumber = startBlockNumber;
		this.endBlockNumber = endBlockNumber;
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

}
