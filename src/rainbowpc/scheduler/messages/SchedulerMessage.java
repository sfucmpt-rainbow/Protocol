package rainbowpc.scheduler.messages;

import rainbowpc.Message;

public abstract class SchedulerMessage extends Message {
	public SchedulerMessage(String method) {
		super("scheduler", method);
	}
}
