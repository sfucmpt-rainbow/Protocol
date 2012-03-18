/*
 * 
 * Class to be removed -- Depreciated
 * 
 */
package rainbowpc.controller.messages.depreciated;

import rainbowpc.controller.messages.ControllerMessage;

public class NodeDisconnectMessage extends ControllerMessage {
	public static final String LABEL = "node_disconnect";
	private String id;

	public NodeDisconnectMessage(String id) {
		super(LABEL);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
