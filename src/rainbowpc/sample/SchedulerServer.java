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
			final SchedulerProtocol protocol = new SchedulerProtocol();
			executor.execute(protocol);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					protocol.shutdown();
					Thread handle = new Thread(protocol);
					byte retries = 0;
					final byte totalRetries = 5;
					while (retries < totalRetries && !protocol.hasExited()) {
						handle.interrupt();
						System.out.println("Waiting...");
						retries++;
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e){}
					}
					if (retries < 5) 
						System.out.println("Graceful shutdown completed!");
					else
						System.out.println("Graceful shutdown failed, abort, abort!");
				}	
			});
			while (true) {
				protocol.pollMessage();
			}
		}
		catch (IOException e) {
			System.out.println("Failed to start protocol, aborting...");
			System.exit(1);
		}
		
	}
}
