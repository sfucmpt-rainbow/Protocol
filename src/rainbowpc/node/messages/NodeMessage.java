package rainbowpc.node.messages;

import rainbowpc.Message;

public abstract class NodeMessage extends Message {
	public NodeMessage(String method) {
		super("node", method);
	}
}
