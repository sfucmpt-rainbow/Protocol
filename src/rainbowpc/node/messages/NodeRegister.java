package rainbowpc.node.messages;

public class NodeRegister extends NodeMessage {
	public static final String LABEL = "register";
	private int cores;

	public NodeRegister() {
		super(LABEL);
		cores = Runtime.getRuntime().availableProcessors();
	}

	public int getCoreCount() {
		return cores;
	}
}
