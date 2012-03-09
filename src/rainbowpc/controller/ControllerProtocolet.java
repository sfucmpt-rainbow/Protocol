package rainbowpc.controller;

import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
import rainbowpc.RainbowException;
import rainbowpc.Message;
import rainbowpc.RpcAction;
import rainbowpc.controller.messages.NewNodeMessage;
import java.net.Socket;
import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ControllerProtocolet extends Protocol implements Protocolet {
	String id;

	public ControllerProtocolet(Socket socket, LinkedBlockingQueue<Message> sharedQueue) throws IOException {
		super(socket);
		messageQueue = sharedQueue;
		id = generateIdBySocket(socket);
		queueMessage(new NewNodeMessage(id));     // Notifies application that a new node has joined
		log("Protocolet spawned for " + getId());
	}

	private String generateIdBySocket(Socket socket) {
		return "node-" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
	}

	@Override
	protected void shutdownCallable() {
	}

	public String getId() {
		return id;
	}

	public int queueSize() {
		return 0;
	}

	public int compareTo(Protocolet second) {
		return 0;
	}
}
