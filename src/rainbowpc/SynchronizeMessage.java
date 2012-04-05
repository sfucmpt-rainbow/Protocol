package rainbowpc;

public class SynchronizeMessage extends Message {
	private static final String type	= "generic";
	public static final String LABEL	= "synchronize";
	private int uniqueId;

	public SynchronizeMessage(int uniqueId) {
		super(type, LABEL);
		this.uniqueId = uniqueId;
	}

	public int getId() {
		return uniqueId;
	}
}
