package rainbowpc.scheduler;

import rainbowpc.Message;

public abstract class SchedulerMessage extends Message {
	public SchedulerMessage() {
		this.type = "scheduler";
	}
}
