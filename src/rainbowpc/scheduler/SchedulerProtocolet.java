package rainbowpc.scheduler;

import rainbowpc.scheduler.messages.SchedulerMessage;
import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import rainbowpc.Message;
import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.scheduler.messages.CacheReady;
import rainbowpc.scheduler.messages.CacheRelease;
import rainbowpc.scheduler.messages.CacheRequest;
import rainbowpc.scheduler.messages.QueryFound;
import rainbowpc.scheduler.messages.WorkBlockComplete;

public class SchedulerProtocolet extends Protocol implements Protocol.Protocolet {

	String id;
	/*
	 * Fields are unused and coverup parent fields Should be removed private
	 * Socket socket; private BufferedReader instream; private PrintWriter
	 * ostream;
	 */
	private LinkedBlockingQueue<Message> sharedQueue;
	private SchedulerProtocol schedulerProtocol;

	public SchedulerProtocolet(
			Socket socket,
			LinkedBlockingQueue<Message> sharedQueue,
			SchedulerProtocol schedulerProtocol) throws IOException {
		super(socket);
		this.schedulerProtocol = schedulerProtocol;
		this.sharedQueue = sharedQueue;
		// Set to null to ensure it is not used and will throw an error if it is
		this.messageQueue = null;

		id = generateIdBySocket(socket);
		initLogger();
		log("Handler spawned for " + id);
		sendMessage(new ControllerBootstrapMessage(id));
		log("Bootstrap message sent");
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		// Is there a better way to do this?
		// using this within an anonymous RpcAction refers to the rpc action
		final SchedulerProtocolet instance = this;
		rpcMap.put(CacheReady.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				sharedQueue.add(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheReady.class, CacheReady.LABEL, instance));
			}
		});
		rpcMap.put(CacheRelease.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				sharedQueue.add(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheRelease.class, CacheRelease.LABEL, instance));
			}
		});
		rpcMap.put(CacheRequest.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				sharedQueue.add(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheRequest.class, CacheRequest.LABEL, instance));
			}
		});
		rpcMap.put(QueryFound.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				sharedQueue.add(SchedulerMessage.createSchedulerMessage(jsonRaw, QueryFound.class, QueryFound.LABEL, instance));
			}
		});
		rpcMap.put(WorkBlockComplete.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				sharedQueue.add(SchedulerMessage.createSchedulerMessage(jsonRaw, WorkBlockComplete.class, WorkBlockComplete.LABEL, instance));
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
