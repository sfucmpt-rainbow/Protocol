package rainbowpc.protocols;

import java.net.Socket;
import java.io.IOException;
import rainbowpc.Protocol;
import rainbowpc.Message;

public class NodeProtocol extends Protocol {
	private String id;

	public NodeProtocol(String host) throws IOException {
		super(host);
		this.id = this.register();
	}
	public NodeProtocol(String host, int port) throws IOException {
		super(host, port);
		this.id = this.register();
	}
	public NodeProtocol(Socket socket) throws IOException {
		super(socket);
		this.id = this.register();
	}

	private String register() throws IOException {
		String response = this.sendMessage(new RegisterMessage(), Protocol.WAIT);
		return translator.fromJson(response, String.class);
	}

	private class RegisterMessage extends Message {
		public RegisterMessage() {
			this.type = "node";
			this.method = "register";
		}
	}
}
