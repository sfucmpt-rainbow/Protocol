/*
 * 
 * Class to be removed -- Depreciated
 * 
 */
package rainbowpc.controller.messages.depreciated;

import rainbowpc.controller.ControllerProtocolet;
import rainbowpc.controller.messages.ControllerMessage;

public class NewNodeMessage extends ControllerMessage {
	public static final String LABEL = "new_node";
	private String name;
	private int coreCount;
	private transient ControllerProtocolet agent;

	public NewNodeMessage(String name, int coreCount) {
		this(name, coreCount, null);
	}

	public NewNodeMessage(String name, int coreCount, ControllerProtocolet agent) {
		super(LABEL);
		this.name = name;
		this.coreCount = coreCount;
		this.agent = agent;
	}

	public String getName() {
		return name;
	}

	public int getCoreCount() {
		return coreCount;
	}

	public ControllerProtocolet getAgent() {
		return agent;
	}
}
