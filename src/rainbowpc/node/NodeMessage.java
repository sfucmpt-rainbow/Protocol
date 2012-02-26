package rainbowpc.node;

import rainbowpc.Message;

public abstract class NodeMessage extends Message {
	public NodeMessage(String method) {
		super("node", method);
	}
}
