package rainbowpc.scheduler;

import rainbowpc.scheduler.messages.SchedulerMessage;
import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import rainbowpc.Message;
import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.scheduler.messages.*;
import rainbowpc.SynchronizeMessage;

public class SchedulerProtocolet extends Protocol implements Protocol.Protocolet {

	String id;

	private int syncTokens = 0;
	private int waitingToken;
	private AtomicBoolean waitingSync = new AtomicBoolean(false);
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

	private void addToQueue(Message message) {
		if (!waitingSync.get()) {
			sharedQueue.add(message);
		} else {
			warn("Message dropped waiting for state convergence");
		}
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
				addToQueue(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheReady.class, CacheReady.LABEL, instance));
			}
		});
		rpcMap.put(CacheRelease.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				addToQueue(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheRelease.class, CacheRelease.LABEL, instance));
			}
		});
		rpcMap.put(CacheRequest.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				addToQueue(SchedulerMessage.createSchedulerMessage(jsonRaw, CacheRequest.class, CacheRequest.LABEL, instance));
			}
		});
		rpcMap.put(QueryFound.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				addToQueue(SchedulerMessage.createSchedulerMessage(jsonRaw, QueryFound.class, QueryFound.LABEL, instance));
			}
		});
		rpcMap.put(WorkBlockComplete.LABEL, new RpcAction() {

			@Override
			public void action(String jsonRaw) {
				addToQueue(SchedulerMessage.createSchedulerMessage(jsonRaw, WorkBlockComplete.class, WorkBlockComplete.LABEL, instance));
			}
		});
		rpcMap.put(SynchronizeMessage.LABEL, new RpcAction() {
			@Override
			public void action(String jsonRaw) {
				unwaitSync(Message.createMessage(jsonRaw, SynchronizeMessage.class, SynchronizeMessage.LABEL));
			}
		});
	}

	@Override
	protected void shutdownCallable() {
		sharedQueue.add(new ControllerDisconnect(this));
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
	
	@Override
	public synchronized void synchronize() {
		int token = syncTokens++;
		waitingToken = token;
		waitingSync.set(true);
		log("Waiting for state convergence");
		handledSendMessage(new SynchronizeMessage(token));
	}

	private synchronized void unwaitSync(SynchronizeMessage sync) {
		if (sync.getId() == waitingToken) {
			waitingSync.set(false);
			log("Synchronized state achieved!");
		} else {
			warn("Outdated token received but ignored");
		}
	}

	public String toString() {
		return getId();
	}
}
