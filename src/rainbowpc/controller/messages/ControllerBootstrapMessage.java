package rainbowpc.controller.messages;

import rainbowpc.controller.ControllerMessage;

public class ControllerBootstrapMessage extends ControllerMessage {
	public static final String LABEL = "bootstrap";
	public String id;
	
	public ControllerBootstrapMessage(String id) {
		super(LABEL);
		this.id = id;
	}
}
