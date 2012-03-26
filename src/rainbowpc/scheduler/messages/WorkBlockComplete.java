/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowpc.scheduler.messages;

import rainbowpc.controller.messages.WorkBlockSetup;

/**
 *
 * @author WesleyLuk
 */
public class WorkBlockComplete extends SchedulerMessage {

	public static final String LABEL = "workBlockComplete";
	
	private int stringLength;
	private long startBlockNumber;
	private long endBlockNumber;
	private int queryID;
	public WorkBlockComplete(String id, int queryID, WorkBlockSetup request) {
		super(LABEL, id);
		this.stringLength = request.getStringLength();
		this.startBlockNumber = request.getStartBlockNumber();
		this.endBlockNumber = request.getEndBlockNumber();
		this.queryID = queryID;
	}

	public long getEndBlockNumber() {
		return endBlockNumber;
	}

	public long getStartBlockNumber() {
		return startBlockNumber;
	}

	public int getStringLength() {
		return stringLength;
	}

	public int getQueryID() {
		return queryID;
	}
	
}
