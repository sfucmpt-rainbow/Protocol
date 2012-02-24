package rainbowpc.node;

import rainbowpc.Message;

public abstract class NodeMessage extends Message {
	NodeMessage() {
		super();
	}

	protected void setType() {
		type = "node";
	}
}
