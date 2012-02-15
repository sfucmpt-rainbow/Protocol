package rainbowpc.protocols;

import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import rainbowpc.Protocol;
import rainbowpc.Message;
import rainbowpc.RainbowException;

public class NodeProtocol extends Protocol {
	private final static int DEFAULT_CONTROL_PORT = 7001;

	private String id;

	public NodeProtocol(String host) throws IOException, RainbowException {
		super(host);
		this.register();
	}
	public NodeProtocol(String host, int port) throws IOException, RainbowException {
		super(host, port);
		this.register();
	}
	public NodeProtocol(Socket socket) throws IOException, RainbowException {
		super(socket);
		this.register();
	}

	private String register() throws IOException, RainbowException {
		String response = this.sendMessage(new RegisterMessage(), Protocol.WAIT);
		ControllerListMessage result = translator.fromJson(response, ControllerListMessage.class);
		shutdown();
		this.id = bindToController(result.controllers);
		if (this.id == null) {
			throw new RainbowException("No controller provided by the Service Gateway accepted register.");
		}
		return translator.fromJson(response, String.class);
	}

	private String bindToController(ArrayList<String> controllers) throws IOException {
		for (String address : controllers) {
			String[] ipTuple = address.split(":");
			String ip = ipTuple[0];
			int port = ipTuple.length > 1? Integer.parseInt(ipTuple[1]) : DEFAULT_CONTROL_PORT;
			initBuffers(new Socket(ip, port));
			String response = this.sendMessage(new RegisterMessage(), Protocol.WAIT);
			RegisterResponse result = translator.fromJson(response, RegisterResponse.class);
			if (result.accepted) {
				return result.id;
			}
			shutdown();
		}
		return null;
	}

	//////////////////////////////////////////////////////////////
	// Query/Outgoing messages
	//
	private class NodeMessage extends Message {
		public NodeMessage() {
			this.type = "node";	
		}
	}

	private class RegisterMessage extends NodeMessage {
		public RegisterMessage() {
			this.type = "node";
			this.method = "register";
		}
	}
	
	//////////////////////////////////////////////////////////////
	// Response/Incoming messages
	//
	private class ControllerListMessage extends Message {
		public ArrayList<String> controllers;
		public ControllerListMessage() {}
	}

	private class RegisterResponse extends Message {
		public String id;
		public boolean accepted;
		public RegisterResponse() {}
	}
}
