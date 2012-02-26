/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.scheduler.messages;

/**
 *
 * @author WesleyLuk
 */
public class WorkBlockComplete extends SchedulerMessage{
	
	public static final String LABEL = "workcomplete";
	int blockSize;
	public WorkBlockComplete(int blockSize) {
		super(LABEL);
		this.blockSize = blockSize;
	}
}
