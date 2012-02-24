package rainbowpc.controller;

import rainbowpc.Message;

public class ControllerMessage extends Message {
	public ControllerMessage() {
		super();
	}

	protected void setType() {
		type = "controller";
	}
}
