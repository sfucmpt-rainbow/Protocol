package rainbowpc.scheduler;

import rainbowpc.scheduler.messages.SchedulerMessage;
import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
import rainbowpc.Message;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import rainbowpc.scheduler.messages.NewControllerMessage;

public class SchedulerProtocol extends Protocol {

	private static final int DEFAULT_PORT = 7001;
	private static final int DEFAULT_BLOCKSIZE = 1000000; //1M
	private static final int SOCKET_BLOCK_MILLIS = 1000;
	// This is an externally initialized queue
	private final TreeMap<String, Protocolet> handlers = new TreeMap<String, Protocolet>();
	private ServerSocket greeter;
	private int blockSize;

	// Scheduler is unique in the sense that it doesn't make connections,
	// a socket must be passed to the rpc handler.
	public SchedulerProtocol() throws IOException {
		this(DEFAULT_PORT);
	}

	public SchedulerProtocol(int port) throws IOException {
		this(port, DEFAULT_BLOCKSIZE);
	}

	public SchedulerProtocol(int port, int blockSize) throws IOException {
		this.blockSize = blockSize;
		greeter = new ServerSocket(port);
		greeter.setSoTimeout(SOCKET_BLOCK_MILLIS);
	}

	/*
	 * Unused since everything is handed in protocolets
	 */
	protected void initRpcMap() {
		//rpcMap = new TreeMap<String, RpcAction>();
	}

	@Override
	public void run() {
		while (!terminated) {
			try {
				Socket socket = greeter.accept();
				log("Accepted new client");
				SchedulerProtocolet handler = new SchedulerProtocolet(socket, messageQueue, this);
				handlers.put(handler.getId(), handler);  // blargh, java...				
				/*
				 * Internal message to inform something listening on the queue
				 * that a new controller has connected Must be done here because
				 * a call to getControllerHandle should not return null and
				 * adding this to the queue could cause it to call immediately
				 * afterwards
				 */
				messageQueue.add(new NewControllerMessage(handler));
				new Thread(handler).start();
			} catch (SocketTimeoutException e) {
			} catch (SocketException e) {
				shutdown();
			} catch (IOException e) {
				e.printStackTrace();
				shutdown();
			}
			//catch (RainbowException e) {
			//}
		}
		log("Final size: " + handlers.size());
		log("Queue size: " + messageQueue.size());
		exited = true;
		log("Protocol succesfully ended");
	}

	public Protocolet getControllerHandle(String id) {
		return handlers.get(id);
	}

	public Protocolet removeControllerHandle(String id) {
		return handlers.remove(id);
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
		} catch (IOException e) {
		}
		terminated = true;
		log("Terminated");
	}

	@Override
	public void sendMessage(Message msg) throws IOException {
		throw new IOException("Scheduler not connected to any server");
	}
}
