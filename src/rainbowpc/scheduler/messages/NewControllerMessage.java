/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.scheduler.messages;

import rainbowpc.scheduler.SchedulerProtocolet;

/**
 *
 * @author WesleyLuk
 */
public class NewControllerMessage extends SchedulerMessage {

	public static final String LABEL = "newcontroller";

	public NewControllerMessage(SchedulerProtocolet protocolet) {
		super(LABEL, protocolet);
	}
}
