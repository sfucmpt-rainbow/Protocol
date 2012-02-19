package rainbowpc.scheduler;

import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.RainbowException;
import rainbowpc.scheduler.SchedulerMessage;
import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerProtocol extends Protocol {
	// This is an externally initialized queue
	ConcurrentLinkedQueue<SchedulerMessage> sharedQueue;

	// Scheduler is unique in the sense that it doesn't make connections,
	// a socket must be passed to the rpc handler.
	public SchedulerProtocol(Socket socket, ConcurrentLinkedQueue<SchedulerMessage> sharedQueue) throws IOException {
		super(socket);
		this.sharedQueue = sharedQueue;
	}

	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();

		rpcMap.put("register", new RpcAction() {
			public void action(String rawJson) {
			}
		});
	}

	
}
