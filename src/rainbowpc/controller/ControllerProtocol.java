package rainbowpc.controller;

import rainbowpc.controller.messages.ControllerBootstrapMessage;
import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import java.util.TreeMap;
import java.io.IOException;
import rainbowpc.Message;
import rainbowpc.controller.messages.*;

public class ControllerProtocol extends Protocol {

	private static final int DEFAULT_SCHEDULER_PORT = 7001;
	private static final int DEFAULT_LISTEN_PORT = 7002;
	private String id;
	Thread subscriber;

	public String getId() {
		return id;
	}

	public ControllerProtocol(String schedulerHost, Thread subscriber) throws IOException {
		this(schedulerHost, DEFAULT_SCHEDULER_PORT, subscriber);
	}

	public ControllerProtocol(String schedulerHost, int schedulerPort, Thread subscriber) throws IOException {
		this(schedulerHost, schedulerPort, DEFAULT_LISTEN_PORT, subscriber);
	}

	public ControllerProtocol(String schedulerHost, int schedulerPort, int listenPort, Thread subscriber) throws IOException {
		super(schedulerHost, schedulerPort);
		this.subscriber = subscriber;
	}

	@Override
	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();
		/*
		 * Have to be careful and make sure we store the id that the scheduler
		 * gives us
		 */
		rpcMap.put(ControllerBootstrapMessage.LABEL, new RpcAction() {

			public void action(String rawJson) {
				ControllerBootstrapMessage bootstrap = Message.createMessage(rawJson, ControllerBootstrapMessage.class, ControllerBootstrapMessage.LABEL);
				id = bootstrap.id;
				queueMessage(bootstrap);
			}
		});
		rpcMap.put(WorkBlockSetup.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, WorkBlockSetup.class, WorkBlockSetup.LABEL));
			}
		});
		rpcMap.put(CacheRequestResponse.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, CacheRequestResponse.class, CacheRequestResponse.LABEL));
			}
		});
		rpcMap.put(ControllerBootstrapMessage.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, ControllerBootstrapMessage.class, ControllerBootstrapMessage.LABEL));
			}
		});
		rpcMap.put(NewQuery.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				queueMessage(Message.createMessage(rawJson, NewQuery.class, NewQuery.LABEL));
			}
		});
		rpcMap.put(StopQuery.LABEL, new RpcAction() {

			@Override
			public void action(String rawJson) {
				messageQueue.clear();			
				queueMessage(Message.createMessage(rawJson, StopQuery.class, StopQuery.LABEL));
				subscriber.interrupt();
			}
		});
	}

	@Override
	protected void shutdownCallable() {
		log("Shutting down greeter...");
	}
}
