package rainbowpc.controller;

import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.controller.ControllerProtocolet;
import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import rainbowpc.Message;
import rainbowpc.controller.messages.WorkBlockSetup;

public class ControllerProtocol extends Protocol {

	private static final int DEFAULT_SCHEDULER_PORT = 7001;
	private static final int DEFAULT_LISTEN_PORT = 7002;
	private String id;
	private ExecutorService greeterExecutor;
	private NodeGreeter greeter;

	public String getId() {
		return id;
	}

	public ControllerProtocol(String schedulerHost) throws IOException {
		this(schedulerHost, DEFAULT_SCHEDULER_PORT);
	}

	public ControllerProtocol(String schedulerHost, int schedulerPort) throws IOException {
		this(schedulerHost, schedulerPort, DEFAULT_LISTEN_PORT);
	}

	public ControllerProtocol(String schedulerHost, int schedulerPort, int listenPort) throws IOException {
		super(schedulerHost, schedulerPort);
		greeter = new NodeGreeter(listenPort);
		greeterExecutor = Executors.newSingleThreadExecutor();
		greeterExecutor.execute(greeter);
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		/*
		 * Have to be careful and make sure we store the id that the scheduler
		 * gives us
		 */
		rpcMap.put(ControllerBootstrapMessage.LABEL, new RpcAction() {

			public void action(String rawJson) {
				ControllerBootstrapMessage bootstrap = Message.createMessage(rawJson, ControllerBootstrapMessage.class, ControllerBootstrapMessage.LABEL);
				id = bootstrap.id;
				queueMessage(bootstrap);
			}
		});
		rpcMap.put(WorkBlockSetup.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, WorkBlockSetup.class, WorkBlockSetup.LABEL));
			}
		});
	}

	@Override
	protected void shutdownCallable() {
		log("Shutting down greeter...");
		greeter.terminate();
		greeterExecutor.shutdown();
	}

	private class NodeGreeter implements Runnable  {
		private static final int SOCKET_WAIT_MILLIS = 1000;
		private ServerSocket greeter;

		public NodeGreeter(int port) throws IOException {
			greeter = new ServerSocket(port);
			greeter.setSoTimeout(SOCKET_WAIT_MILLIS);
		}

		public void run() {
			while (!terminated) {
				try {
					Socket socket = greeter.accept();
					new ControllerProtocolet(socket);
				}
				catch (IOException e) {
				}
			}
			try {
				greeter.close();
			}
			catch (IOException e) {}
			log("Greeter terminated");
		}

		public void terminate() {
			terminated = true;
		}
		
	}
}
