package rainbowpc.scheduler;

import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.RainbowException;
import rainbowpc.scheduler.SchedulerMessage;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.TreeMap;
import java.util.TreeSet;
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
		while (true) {
			try {
				Socket socket = greeter.accept();
				Protocolet handler = new Protocolet(socket, sharedQueue);
				handlers.add(handler);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (RainbowException e) {
			}
		}
	}
	
	@Override
	public boolean isAlive() {
		return greeter.isBound();
	}

	///////////////////////////////////////////////////////////
	// Microprotocol to handle incoming connections
	//
	private class Protocolet implements Runnable, Comparable<Protocolet> {
		private String id;
		private Socket socket;
		private BufferedReader instream;
		private PrintWriter ostream;
		private ConcurrentLinkedQueue<SchedulerMessage> sharedQueue;
		
		// bootstrap
		public Protocolet(Socket socket, ConcurrentLinkedQueue<SchedulerMessage> sharedQueue) throws RainbowException {
			this.sharedQueue = sharedQueue;
			this.socket = socket;
			
			try {
				initBuffers(socket);
				// failed bootstrap condition
				if (!bootstrapController()) {
					throw new RainbowException("Failed to bootstrap with node " + id);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new RainbowException("Failed to bootstrap controller " + id);
			}
		}

		private void initBuffers(Socket socket) throws IOException {
			instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ostream = new PrintWriter(socket.getOutputStream(), true);
		}

		private boolean bootstrapController() throws IOException {
			Header header = new Header(instream.readLine());
			JsonElement data = translator.fromJson(instream.readLine(), JsonElement.class);
			boolean bootstrapped = false;
			if (header.isAcceptedVersion()) {
				id = socket.getInetAddress().toString();
				sendMessage("register", new RegisterReplyMessage(id));
				bootstrapped = true;
			}
			return bootstrapped;
		}

		public void run() {
		}

		public int compareTo(Protocolet second) {
			return id.compareTo(second.id);
		}

		public void shutdown() {
			try {
				instream.close();
				ostream.close();
				socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
