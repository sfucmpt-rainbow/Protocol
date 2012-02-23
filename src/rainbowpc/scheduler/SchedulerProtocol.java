package rainbowpc.scheduler;

import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
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
		while (true) {
			try {
				Socket socket = greeter.accept();
				//Protocolet handler = new Protocolet(socket, sharedQueue);
				//handlers.add(handler);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			//catch (RainbowException e) {
			//}
		}
	}
	
	@Override
	public boolean isAlive() {
		return greeter.isBound();
	}

	public static void main(String[] args) throws IOException {
		SchedulerProtocol foo = new SchedulerProtocol();
	}
}
