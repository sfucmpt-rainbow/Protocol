package rainbowpc.node;

import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import com.google.gson.JsonElement;
import rainbowpc.Protocol;
import rainbowpc.RainbowException;
import rainbowpc.node.messages.*;
import rainbowpc.RpcAction;
import rainbowpc.Message;

public class NodeProtocol extends Protocol {
	private final static int DEFAULT_CONTROL_PORT = 7002;

	public NodeProtocol(String host) throws IOException, RainbowException {
		this(host, DEFAULT_CONTROL_PORT);
	}
	public NodeProtocol(String host, int port) throws IOException, RainbowException {
		this(new Socket(host, port));
	}
	public NodeProtocol(Socket socket) throws IOException, RainbowException {
		super(socket);
		register();
	}

	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		rpcMap.put(WorkMessage.LABEL, new RpcAction() {
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, WorkMessage.class, WorkMessage.LABEL));
			}
		});
	}

	/////////////////////////////////////////////////////////////////
	// Bootstrapping code
	//
	private void register() throws IOException, RainbowException {
		sendMessage(new RegisterMessage());
	}

	//////////////////////////////////////////////////////////////
	// External interaction interface
	//

	//////////////////////////////////////////////////////////////
	// Query/Outgoing messages
	//
	private class RegisterMessage extends NodeMessage {
		public static final String LABEL = "register";
		private int cores;
		public RegisterMessage() {
			super(LABEL);
			cores = Runtime.getRuntime().availableProcessors();
		}
	}
	
	//////////////////////////////////////////////////////////////
	// Response/Incoming messages
	//
	private class BootstrapMessage extends NodeMessage {
		public static final String LABEL = "bootstrap";
		public String id;
		public boolean accepted;
		public BootstrapMessage() {
			super(LABEL);
		}
	}
}
