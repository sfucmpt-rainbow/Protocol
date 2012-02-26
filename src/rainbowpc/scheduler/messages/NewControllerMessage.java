/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.scheduler.messages;

import rainbowpc.Message;

/**
 *
 * @author WesleyLuk
 */
public class NewControllerMessage extends SchedulerMessage {

	public static final String LABEL = "newcontroller";
	public String id;

	public NewControllerMessage(String id) {
		super(LABEL);
		this.id = id;
	}
}
