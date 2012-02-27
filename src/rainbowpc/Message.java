package rainbowpc;

import com.google.gson.Gson;
import rainbowpc.scheduler.SchedulerProtocolet;
import rainbowpc.scheduler.messages.SchedulerMessage;

public abstract class Message {

	/*
	 * See comment in SchedulerMessage
	 */
	protected static final Gson translator = new Gson();
	/*
	 * Factory method for de serializing Message, needed since method is
	 * transient May want to concider just moving the method into the json data
	 * and removing the transient keyword to simplify
	 *
	 */

	public static <T extends Message> T createMessage(String rawJson, Class 
		<T> messageClass, String method) {
		T message = (T) translator.fromJson(rawJson, messageClass);
		message.setMethod(method);
		return message;
	}
	protected String type = null;
	protected transient String method = null;

	public Message(String type, String method) {
		setType(type);
		setMethod(method);
	}

	protected void setType(String type) {
		this.type = type;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}
}
