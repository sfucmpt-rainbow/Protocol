package rainbowpc.node;

import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import com.google.gson.JsonElement;
import rainbowpc.Protocol;
import rainbowpc.RainbowException;
import rainbowpc.node.NodeMessage;
import rainbowpc.node.WorkMessage;
import rainbowpc.RpcAction;

public class NodeProtocol extends Protocol {
	private final static int DEFAULT_CONTROL_PORT = 7001;

	public NodeProtocol(String host) throws IOException, RainbowException {
		super(host);
		register();
	}
	public NodeProtocol(String host, int port) throws IOException, RainbowException {
		super(host, port);
		register();
	}
	public NodeProtocol(Socket socket) throws IOException, RainbowException {
		super(socket);
		register();
	}

	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		rpcMap.put("workOrder", new RpcAction() {
			public void action(String jsonRaw) {
				queueMessage(translator.fromJson(jsonRaw, WorkMessage.class));
			}
		});
		rpcMap.put("bootstrap", new RpcAction() {
			public void action(String jsonRaw) {
				queueMessage(translator.fromJson(jsonRaw, BootstrapMessage.class));
			}
		});
	}

	/////////////////////////////////////////////////////////////////
	// Bootstrapping code
	//
	//private String register() throws IOException, RainbowException {
	//	String response = this.sendMessage(new RegisterMessage(), Protocol.WAIT);
	//	ControllerListMessage result = translator.fromJson(response, ControllerListMessage.class);
	//	shutdown();
	//	this.id = bindToController(result.controllers);
	//	if (this.id == null) {
	//		throw new RainbowException("No controller provided by the Service Gateway accepted register.");
	//	}
	//	return translator.fromJson(response, String.class);
	//}

	//private String bindToController(ArrayList<String> controllers) throws IOException {
	//	for (String address : controllers) {
	//		String[] ipTuple = address.split(":");
	//		String ip = ipTuple[0];
	//		int port = ipTuple.length > 1? Integer.parseInt(ipTuple[1]) : DEFAULT_CONTROL_PORT;
	//		initBuffers(new Socket(ip, port));
	//		String response = this.sendMessage(new RegisterMessage(), Protocol.WAIT);
	//		RegisterResponse result = translator.fromJson(response, RegisterResponse.class);
	//		if (result.accepted) {
	//			return result.id;
	//		}
	//		shutdown();
	//	}
	//	return null;
	//}
	private void register() throws IOException, RainbowException {
		sendMessage("register", new RegisterMessage());
	}

	//////////////////////////////////////////////////////////////
	// External interaction interface
	//

	//////////////////////////////////////////////////////////////
	// Query/Outgoing messages
	//
	private class RegisterMessage extends NodeMessage {
		private int cores;
		public RegisterMessage() {
			super();
			cores = Runtime.getRuntime().availableProcessors();
		}
	}
	
	//////////////////////////////////////////////////////////////
	// Response/Incoming messages
	//
	private class BootstrapMessage extends NodeMessage {
		public String id;
		public boolean accepted;
		public BootstrapMessage() {}
	}
}
