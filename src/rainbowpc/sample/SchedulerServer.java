package rainbowpc.sample;

import rainbowpc.scheduler.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import rainbowpc.controller.messages.WorkBlockSetup;
import rainbowpc.scheduler.messages.CacheReady;
import rainbowpc.scheduler.messages.CacheRelease;
import rainbowpc.scheduler.messages.CacheRequest;
import rainbowpc.scheduler.messages.NewControllerMessage;
import rainbowpc.scheduler.messages.QueryFound;
import rainbowpc.scheduler.messages.SchedulerMessage;
import rainbowpc.scheduler.messages.WorkBlockComplete;

public class SchedulerServer extends Thread {

	Executor executor;
	SchedulerProtocol protocol;

	public SchedulerServer() {
		try {
			executor = Executors.newSingleThreadExecutor();
			protocol = new SchedulerProtocol();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		buildHook();
	}

	public void callInterrupt() {
		this.interrupt();
	}

	public void buildHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				// this.interrupt() would be ambiguous
				callInterrupt();
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
					} catch (InterruptedException e) {
					}
				}
				if (retries < 5) {
					System.out.println("Graceful shutdown completed!");
				} else {
					System.out.println("Graceful shutdown failed, abort, abort!");
				}
			}
		});
	}

	public void start() {
		super.start();
		executor.execute(protocol);
	}
	// simulates a scheduler server

	public void run() {
		while (true) {
			try {
				SchedulerMessage message = (SchedulerMessage) protocol.getMessage();
				switch (message.getMethod()) {
					case CacheReady.LABEL:
					case CacheRelease.LABEL:
					case CacheRequest.LABEL:
					case QueryFound.LABEL:
						System.out.println("Got message " + message.getMethod());
						break;
					case NewControllerMessage.LABEL:
						System.out.println("There is a new controller " + message.getID());
						System.out.println("Sending random work packet");
						try {
							message.getSchedulerProtocolet().sendMessage(new WorkBlockSetup(0,0,0));
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("Could not send a work block to controller " + message.getID());
						}
						break;
					case WorkBlockComplete.LABEL:
						System.out.println("Work block is complete, telling remote to shutdown");
						message.getSchedulerProtocolet().shutdown();
						break;
					default:
						System.out.println("Unexpected message " + message);
						break;
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				System.out.println("Thread was interrupted, exiting");
				break;
			}
		}
		System.exit(0);
	}

	public static void main(String[] args) {
		new SchedulerServer().start();
	}
}
