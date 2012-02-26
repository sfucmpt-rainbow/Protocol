package rainbowpc.scheduler;

import rainbowpc.Message;

public abstract class SchedulerMessage extends Message {
	public SchedulerMessage(String method) {
		super("scheduler", method);
	}
}
