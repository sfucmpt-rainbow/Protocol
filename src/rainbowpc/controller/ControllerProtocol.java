package rainbowpc.controller;

import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.Protocol;
import rainbowpc.Message;
import rainbowpc.RpcAction;
import rainbowpc.Protocol.Protocolet;
import java.util.TreeMap;
import java.io.IOException;
import rainbowpc.controller.messages.ControllerMessage;
import rainbowpc.controller.messages.ControllerShutdownMessage;
import rainbowpc.controller.messages.WorkBlockSetup;

public class ControllerProtocol extends Protocol {
	private static final int DEFAULT_SCHEDULER_PORT  = 7001;
	private static final int DEFAULT_LISTEN_PORT = 7002;
	
	public ControllerProtocol(String schedulerHost) throws IOException {
		this(schedulerHost, DEFAULT_SCHEDULER_PORT);
	}
	
	public ControllerProtocol(String schedulerHost, int schedulerPort) throws IOException {
		this(schedulerHost, schedulerPort, DEFAULT_LISTEN_PORT);
	}

	public ControllerProtocol(String schedulerHost, int schedulerPort, int listenPort) throws IOException {
		super(schedulerHost, schedulerPort);
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		rpcMap.put(ControllerBootstrapMessage.LABEL, new RpcAction() {
			public void action(String rawJson) {
				queueMessage(translator.fromJson(rawJson, ControllerBootstrapMessage.class));
			}
		});
		rpcMap.put(ControllerShutdownMessage.LABEL, new RpcAction() {
			public void action(String rawJson) {
				queueMessage(translator.fromJson(rawJson, ControllerShutdownMessage.class));
			}
		});
		rpcMap.put(WorkBlockSetup.LABEL, new RpcAction() {
			public void action(String rawJson) {
				queueMessage(translator.fromJson(rawJson, WorkBlockSetup.class));
			}
		});
	}
}
