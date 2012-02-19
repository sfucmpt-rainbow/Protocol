package rainbowpc.node;

import rainbowpc.Message;

public abstract class NodeMessage extends Message {
	NodeMessage() {
		this.type = "node";
	}
}
