package rainbowpc.scheduler.messages;

import com.google.gson.Gson;
import rainbowpc.Message;
import rainbowpc.scheduler.SchedulerProtocolet;

public abstract class SchedulerMessage extends Message {
	/*
	 * Not too sure if we should have a static instance here Putting it here for
	 * now and we can move it or reference the other one if we have problems
	 * Can't use the one in protocol currently since it is protected
	 */

	protected static final Gson translator = new Gson();
	/*
	 * Factory method for de serializing SchedulerMessages Only usable on the
	 * scheduler since only it has access to SchedulerProtocolet Must be in here
	 * since setSchedulerProtocolet is private and has no access outside of the
	 * class
	 */

	@SuppressWarnings("unchecked")
	public static <T extends SchedulerMessage> T createSchedulerMessage(String rawJson, Class<T> messageClass, String method, SchedulerProtocolet sp) {
		SchedulerMessage schedulerMessage = Message.createMessage(rawJson, messageClass, method);
		schedulerMessage.setSchedulerProtocolet(sp);
		return (T)schedulerMessage;
	}
	/*
	 * Id of the controller
	 */
	private String id = null;
	/*
	 * Unused on the controller side, only used on the scheduler
	 */
	private transient SchedulerProtocolet protocolet = null;

	/*
	 * When messages are constructed on the controller side use this constructor
	 */
	public SchedulerMessage(String method, String id) {
		super("scheduler", method);
		this.id = id;
	}
	/*
	 * Constructor should only be used when creating a NewControllerMessage
	 * because only then will we have to use the constructor and have access to
	 * the protocolet
	 */

	public SchedulerMessage(String method, SchedulerProtocolet protocolet) {
		super("scheduler", method);
		this.setSchedulerProtocolet(protocolet);
	}

	/*
	 * Don't allow changes to the SchedulerProtocolet from elsewhere
	 */
	private void setSchedulerProtocolet(SchedulerProtocolet sp) {
		setID(sp.getId());
		protocolet = sp;
	}

	public SchedulerProtocolet getSchedulerProtocolet() {
		return protocolet;
	}
	/*
	 * Don't allow changes to the ID from elsewhere
	 */

	private void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}
}
