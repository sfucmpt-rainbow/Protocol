package rainbowpc.controller.messages;

public class NewNodeMessage extends ControllerMessage {
	public static final String LABEL = "new_node";
	private String name;
	private int coreCount;

	public NewNodeMessage(String name, int coreCount) {
		super(LABEL);
		this.name = name;
		this.coreCount = coreCount;
	}

	public String getName() {
		return name;
	}

	public int getCoreCount() {
		return coreCount;
	}
}
