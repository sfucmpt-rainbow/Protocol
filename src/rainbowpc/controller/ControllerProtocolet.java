package rainbowpc.controller;

import rainbowpc.Protocol;
import rainbowpc.Protocol.Protocolet;
import rainbowpc.RainbowException;
import rainbowpc.Message;
import java.net.Socket;
import java.io.IOException;

public class ControllerProtocolet extends Protocol implements Protocolet {
	String id = "foo";

	public ControllerProtocolet(Socket socket) throws IOException {
		super(socket);
		log("Protocolet spawned for " + getId());
	}

	@Override
	protected void initRpcMap() {
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
