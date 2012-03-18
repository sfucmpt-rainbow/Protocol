package rainbowpc.scheduler.messages;

/**
 *
 * Contains info that the controller should tell the scheduler
 * 
 * numberOfCores - Number of cores on the computer
 * 
 */
public class ControllerBootstrapResponse extends SchedulerMessage {
	public static final String LABEL = "controllerBootstrapResponse";

	private int numberOfCores;

	public ControllerBootstrapResponse(String id, int numberOfCores) {
		super(LABEL, id);
		this.numberOfCores = numberOfCores;
	}

	public int getNumberOfCores() {
		return numberOfCores;
	}

}
