package rainbowpc.scheduler;

import rainbowpc.Protocol;
import rainbowpc.RpcAction;
import rainbowpc.RainbowException;
import rainbowpc.scheduler.SchedulerMessage;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerProtocol extends Protocol {
	// This is an externally initialized queue
	ConcurrentLinkedQueue<SchedulerMessage> sharedQueue;
	String assignedControllerId;

	// Scheduler is unique in the sense that it doesn't make connections,
	// a socket must be passed to the rpc handler.
	public SchedulerProtocol(ConcurrentLinkedQueue<SchedulerMessage> sharedQueue) throws IOException {
		this.sharedQueue = sharedQueue;
	}

	protected void initRpcMap() {
		rpcMap = new TreeMap<String, RpcAction>();

		rpcMap.put("register", new RpcAction() {
			public void action(String rawJson) {
				assignedControllerId = socket.getInetAddress().toString();
				//safeSendMessage("register", new RegisterReplyMessage(assignedControllerId));			
			}
		});
	}

	private class RegisterReplyMessage extends SchedulerMessage {
		String id;

		public RegisterReplyMessage(String id) {
			this.id = id;
		}
	}
	
	@Override
	public boolean isAlive() {
		return true;
	}

	///////////////////////////////////////////////////////////
	// Microprotocol to handle incoming connections
	//
	private class Protocolet implements Runnable {
		String id;
		
		public Protocolet() {
		}

		public void run() {
		}
	}
}
