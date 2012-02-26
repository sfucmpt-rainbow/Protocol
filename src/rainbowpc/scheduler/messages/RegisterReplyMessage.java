/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.scheduler.messages;

import rainbowpc.scheduler.SchedulerMessage;

public class RegisterReplyMessage extends SchedulerMessage {

	public static final String LABEL = "registerReply";
	String id;

	public RegisterReplyMessage(String id) {
		super(LABEL);
		this.id = id;
	}
}
