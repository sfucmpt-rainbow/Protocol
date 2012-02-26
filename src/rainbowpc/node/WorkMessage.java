package rainbowpc.node;

//import rainbowpc.protocols.node.NodeMessage;

public class WorkMessage extends NodeMessage {
	public final static String LABEL = "workParcel";
	public WorkMessage() {
		super(LABEL);
	}
}
