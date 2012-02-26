package rainbowpc.scheduler.messages;

import rainbowpc.Message;

public abstract class SchedulerMessage extends Message {
	// Id of the controller so we can send messages back later

	public String id;

	public SchedulerMessage(String method) {
		super("scheduler", method);
	}

	public SchedulerMessage(String method, String id) {
		super("scheduler", method);
		this.id = id;
	}
}
