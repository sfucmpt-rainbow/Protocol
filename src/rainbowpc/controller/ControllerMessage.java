package rainbowpc.controller;

import rainbowpc.Message;

public abstract class ControllerMessage extends Message {
	public ControllerMessage(String method) {
		super("controller", method);
	}
}
