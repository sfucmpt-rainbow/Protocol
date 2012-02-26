package rainbowpc.scheduler;

import rainbowpc.scheduler.messages.SchedulerMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import rainbowpc.Message;
import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.scheduler.messages.NewControllerMessage;

public class SchedulerProtocolet extends Protocol implements Protocol.Protocolet {

	String id;
	private Socket socket;
	private BufferedReader instream;
	private PrintWriter ostream;
	private LinkedBlockingQueue<Message> sharedQueue;
	private SchedulerProtocol schedulerProtocol;

	public SchedulerProtocolet(
			Socket socket,
			LinkedBlockingQueue<Message> sharedQueue,
			SchedulerProtocol schedulerProtocol) throws IOException {
		super(socket);
		this.schedulerProtocol = schedulerProtocol;

		id = generateIdBySocket(socket);
		initLogger();
		log("Handler spawned for " + id);
		sendMessage(new ControllerBootstrapMessage(id));
		// Internal message to inform something listening on the queue that a new
		// controller has connected
		sharedQueue.add(new NewControllerMessage(generateIdBySocket(socket)));
		log("Bootstrap message sent");
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		rpcMap.put("workBlockSetup", new RpcAction() {

			public void action(String rawJson) {
				log("Received new work block setup request");
			}
		});
	}

	@Override
	protected void shutdownCallable() {
		schedulerProtocol.removeControllerHandle(this.id);
	}

	@Override
	protected void log(String msg) {
		logger.info("(" + (id != null ? id : "new node") + ") " + msg);
	}

	@Override
	protected void warn(String msg) {
		logger.warning("(" + (id != null ? id : "new node") + ") " + msg);
	}

	/*
	 * Needs this apparently or else java gives an error
	 */
	@Override
	public Message getMessage() throws InterruptedException {
		return super.getMessage();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void sendMessage(Message msg) throws IOException {
		super.sendMessage(msg);
	}

	@Override
	public int compareTo(Protocol.Protocolet protocolet) {
		return id.compareTo(protocolet.getId());
	}

	@Override
	public int queueSize() {
		return 0;
	}

	private String generateIdBySocket(Socket socket) {
		return "controller-" + socket.getInetAddress() + ":" + socket.getPort();
	}

	public String toString() {
		return getId();
	}
}