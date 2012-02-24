package rainbowpc.sample;

import rainbowpc.scheduler.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

public class SchedulerServer {
	// simulates a scheduler server
	public static void main(String[] args) {
		try {
			Executor executor = Executors.newSingleThreadExecutor();
			SchedulerProtocol protocol = new SchedulerProtocol();
			executor.execute(protocol);
			while (true) {
				protocol.blockingGetMessage();
			}
		}
		catch (IOException e) {
			System.out.println("Failed to start protocol, aborting...");
			System.exit(1);
		}
		
	}
}
