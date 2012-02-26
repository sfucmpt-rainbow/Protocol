package rainbowpc.sample;

import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.Message;
import rainbowpc.Protocol;
import rainbowpc.controller.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rainbowpc.controller.messages.ControllerShutdownMessage;
import rainbowpc.controller.messages.WorkBlockSetup;
import rainbowpc.scheduler.messages.WorkBlockComplete;

public class ControllerServer extends Thread {

	ExecutorService executor;
	ControllerProtocol protocol;

	public ControllerServer() {
		executor = Executors.newSingleThreadExecutor();
		try {
			protocol = new ControllerProtocol("localhost");
		} catch (IOException e) {
			System.out.println("Could not connect to scheduler, has it been started?");
			System.exit(1);
		}
	}

	@Override
	public void start() {
		super.start();
		executor.execute(protocol);
	}

	@Override
	public void run() {
		while (true) {
			Message message;
			try {
				message = protocol.getMessage();
				if (message instanceof ControllerBootstrapMessage) {
					System.out.println("Bootstrap message found!");
					ControllerBootstrapMessage bootstrap = (ControllerBootstrapMessage) message;
					System.out.println("Scheduler assigned id: " + bootstrap.id);
					System.out.println("All done, great success!");
				} else if (message instanceof WorkBlockSetup) {
					WorkBlockSetup workBlock = (WorkBlockSetup) message;
					System.out.println("Got work block " + workBlock.getBlockSize());
					// Pretend we are doing work
					Thread.sleep(5000);
					System.out.println("Work block complete");
					try {
						protocol.sendMessage(new WorkBlockComplete(workBlock.getBlockSize()));
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("Could not send work block complete message");
					}
				} else if (message instanceof ControllerShutdownMessage) {
					System.out.println("Shutdown message recieved");
					interrupt();
				} else {
					System.out.println("Test failed, bootstrap message not received");
				}
			} catch (InterruptedException ie) {
				interrupt();
				break;
			}
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		protocol.shutdown();
		executor.shutdown();
	}
	public static void main(String[] s){
		new ControllerServer().start();
	}
}
