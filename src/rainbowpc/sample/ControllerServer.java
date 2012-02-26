package rainbowpc.sample;

import rainbowpc.Message;
import rainbowpc.Protocol;
import rainbowpc.controller.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerServer {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			ControllerProtocol protocol = new ControllerProtocol("localhost");
			executor.execute(protocol);
			Message message = protocol.pollMessage();
			if (message instanceof ControllerBootstrapMessage) {	
				System.out.println("Bootstrap message found!");
				ControllerBootstrapMessage bootstrap = (ControllerBootstrapMessage) message;
				System.out.println("Scheduler assigned id: " + bootstrap.id);
				System.out.println("All done, great success!");
			}
			else {
				System.out.println("Test failed, bootstrap message not received");
			}
			protocol.shutdown();
			executor.shutdown();
		}

		catch (IOException e) {
			System.out.println("Could not connect to scheduler, has it been started?");
			System.exit(1);
		}
	}

}
