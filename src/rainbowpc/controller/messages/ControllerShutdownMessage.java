/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.controller.messages;

/**
 *
 * @author WesleyLuk
 */
public class ControllerShutdownMessage extends ControllerMessage{
	public static final String LABEL = "shutdown";

	public ControllerShutdownMessage() {
		super(LABEL);
	}
}
