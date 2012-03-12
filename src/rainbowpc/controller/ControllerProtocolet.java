package rainbowpc.controller;

import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
import rainbowpc.RainbowException;
import rainbowpc.Message;
import rainbowpc.RpcAction;
import rainbowpc.controller.messages.NewNodeMessage;
import rainbowpc.controller.messages.NodeDisconnectMessage;
import rainbowpc.node.messages.*;
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
		log("Protocolet spawned for " + getId());
	}

	private String generateIdBySocket(Socket socket) {
		return "node-" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		
		rpcMap.put(NodeRegister.LABEL, new RpcAction() {
			public void action(String rawJson) {
				NodeRegister nodeInfo = 
					Message.createMessage(rawJson, NodeRegister.class, NodeRegister.LABEL);
				queueMessage(new NewNodeMessage(id, nodeInfo.getCoreCount(), ControllerProtocolet.this));
			}
		});

		rpcMap.put(WorkMessage.LABEL, new RpcAction() {
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, WorkMessage.class, WorkMessage.LABEL));
			}
		});	
	}

	@Override
	protected void shutdownCallable() {
		queueMessage(new NodeDisconnectMessage(id));
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
