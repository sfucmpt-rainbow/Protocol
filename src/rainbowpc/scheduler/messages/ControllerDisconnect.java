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
public class ControllerDisconnect extends SchedulerMessage {

	public static final String LABEL = "controllerDisconnect";
	SchedulerProtocolet protocol;
	public ControllerDisconnect(SchedulerProtocolet protocolet) {
		super(LABEL, protocolet);
		this.protocol = protocolet;
	}

	public SchedulerProtocolet getProtocol() {
		return protocol;
	}
	
}
