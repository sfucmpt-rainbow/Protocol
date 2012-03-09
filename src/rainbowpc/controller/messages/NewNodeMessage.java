package rainbowpc.controller.messages;

public class NewNodeMessage extends ControllerMessage {
	public static final String LABEL = "new_node";
	private String name;

	public NewNodeMessage(String name) {
		super(LABEL);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
