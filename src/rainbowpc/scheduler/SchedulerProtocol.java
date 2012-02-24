package rainbowpc.scheduler;

import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
import rainbowpc.RpcAction;
import rainbowpc.RainbowException;
import rainbowpc.scheduler.SchedulerMessage;
import rainbowpc.Message;
import rainbowpc.controller.ControllerBootstrapMessage;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerProtocol extends Protocol {
	private static final int DEFAULT_PORT = 7001;
	// This is an externally initialized queue
	private final ConcurrentLinkedQueue<SchedulerMessage> sharedQueue = new ConcurrentLinkedQueue<SchedulerMessage>();
	private final TreeSet<Protocolet> handlers = new TreeSet<Protocolet>();

	private ServerSocket greeter;

	// Scheduler is unique in the sense that it doesn't make connections,
	// a socket must be passed to the rpc handler.
	public SchedulerProtocol() throws IOException {
		this(DEFAULT_PORT);
	}

	public SchedulerProtocol(int port) throws IOException {
		greeter = new ServerSocket(port);
	}

	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();

		rpcMap.put("register", new RpcAction() {
			public void action(String rawJson) {
				//assignedControllerId = socket.getInetAddress().toString();
				//safeSendMessage("register", new RegisterReplyMessage(assignedControllerId));			
			}
		});
	}

	private class RegisterReplyMessage extends SchedulerMessage {
		String id;

		public RegisterReplyMessage(String id) {
			this.id = id;
			
		}
	}

	@Override
	public void run() {
		while (!terminated) {
			try {
				Socket socket = greeter.accept();
				log("Accepted new client");
				Protocolet handler = new SchedulerProtocolet(socket, sharedQueue);
				handlers.add(handler);
				new Thread(handler).run();
			}
			catch (SocketException e) {}
			catch (IOException e) {
				e.printStackTrace();
			}
			//catch (RainbowException e) {
			//}
		}
		exited = true;
		log("Protocol succesfully ended");
	}
	
	@Override
	public boolean isAlive() {
		return !terminated && greeter.isBound();
	}

	@Override
	public void shutdown() {
		log("Shutting down...");
		try {
			greeter.close();
		}
		catch (IOException e) {}
		terminated = true;
		log("Terminated");
	}

	@Override
	public Message getMessage() {
		return sharedQueue.poll();
	}

	@Override 
	public synchronized Message blockingGetMessage() {
		Message result = null;
		while (result == null) {
			while (!hasMessages()) {
				try {
					wait();
				}
				catch (InterruptedException e) {}
			}
			result = getMessage();
		}
		return result;
	}

	@Override
	public void sendMessage(String method, Message msg) throws IOException {
		throw new IOException("Scheduler not connected to any server");
	}

	public static void main(String[] args) throws IOException {
		SchedulerProtocol foo = new SchedulerProtocol();
	}

	private class SchedulerProtocolet extends Protocol implements Protocolet {	
		String id;

		private Socket socket;
		private BufferedReader instream;
		private PrintWriter ostream;
		private ConcurrentLinkedQueue<SchedulerMessage> sharedQueue;

		public SchedulerProtocolet(
			Socket socket,
			ConcurrentLinkedQueue<SchedulerMessage> sharedQueue
		) throws IOException {
			super(socket);
			id = socket.getInetAddress().getHostAddress();
			log("Handler spawned for " + id);
			sendMessage("bootstrap", new ControllerBootstrapMessage(id));
			log("Bootstrap message sent");
		}

		@Override
		protected void initRpcMap() {
		}
	
		@Override
		public String getId() {
			return id;
		}

		@Override
		public void sendMessage(String method, Message msg) throws IOException {
			super.sendMessage(method, msg);
		}

		@Override
		public int compareTo(Protocolet protocolet) {
			return id.compareTo(protocolet.getId());
		}

		@Override
		public int queueSize() {
			return 0;
		}
	}
}
