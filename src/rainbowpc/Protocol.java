package rainbowpc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import rainbowpc.Message;

public abstract class Protocol implements Runnable {
	///////////////////////////////////////////////////////////
	// Constant Defines
	//
	/** Internal */
	private static final int DEFAULT_PORT = 7001;
	private static final byte MUTEX = 1;
	private static final byte VERSION = 1;
	/** External */
	public final static boolean WAIT = true;

	///////////////////////////////////////////////////////////
	// attributes
	//
	/** 
	  * We allow the inheritee to decide how they want to setup
	  * the socket and input/output buffers.
	  */
	protected Socket socket = null;             
	protected BufferedReader instream = null;
	protected PrintWriter outstream = null;
	protected ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	protected static final Gson translator = new Gson(); 

	/**
	  * RPC mapping
	  */
	protected Map<String, RpcAction> rpcMap;

	///////////////////////////////////////////////////////////
	// Constructors
	//
	public Protocol() {}          // default constructor, do nothing

	public Protocol(String host) throws IOException {
		this(host, DEFAULT_PORT);
		initRpcMap();
	}

	public Protocol(String host, int port) throws IOException {
		this(new Socket(host, port));
		initRpcMap();
	}		

	public Protocol(Socket socket) throws IOException {
		this.initBuffers(socket);
		initRpcMap();
	}

	protected abstract void initRpcMap();

	///////////////////////////////////////////////////////////
	// Constructor helpers
	//
	protected void initBuffers(Socket socket) throws IOException {
		this.socket = socket;
		this.instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.outstream = new PrintWriter(socket.getOutputStream(), true);
	}

	///////////////////////////////////////////////////////////
	// Main Task: Sucks up messages and queues them
	//
	public void run() {
		while(true) {
			try {
				Header header = new Header(instream.readLine());
				String data = instream.readLine();
				if (header.isAcceptedVersion()) {
					RpcAction rpcAction = rpcMap.get(header.getMethod());
					if (rpcAction != null) {
						rpcAction.run(data);
					}
				}
				// else packet is dropped
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("I/O exception occured in protocol, shutting down.");
				shutdown();
			}
		}
	}

	///////////////////////////////////////////////////////////
	// Message handling methods
	//
	protected String sendMessage(String method, Message msg) throws IOException {
		return this.sendMessage(method, msg, false);
	}

	protected String sendMessage(String method, Message msg, boolean waitForResponse) throws IOException {
		String result = null;
		String payload = buildPayload(VERSION, method, translator.toJson(msg));
		outstream.println(payload);
		if (waitForResponse) {
			result = instream.readLine();
		}
		return result;
	}

	// I don't think we'll need this but I'd rather not allow arbitrary Object encoding.
	// if you must send a non-defined message type, you MUST create a JsonElement yourself!
	protected String sendMessage(String method, JsonElement json) throws IOException {
		String result = null;
		String payload = buildPayload(VERSION, method, translator.toJson(json));
		outstream.println(payload);
		return result;
	}

	protected String buildPayload(int version, String methodType, String data) {
		return version + "|" + methodType + "\n" + data;
	}

	protected void queueMessage(Message msg) {
		messageQueue.add(msg);
	}
	
	///////////////////////////////////////////////////////////
	// Connection handling methods
	//
	public void shutdown() {
		try {
			this.instream.close();
			this.outstream.close();
			this.socket.close();
		}
		catch (IOException e) {
			// do nothing
		}
	}

	public boolean isAlive() {
		return this.socket.isConnected();
	}

	private class Header {
		private byte version;
		private String method;
		public Header(String rawHeader) {
			String[] tuple = rawHeader.split("|");
			version = Byte.parseByte(tuple[0]);
			method = tuple[1];
		}
		
		public boolean isAcceptedVersion() {
			return version <= VERSION;
		}

		public String getMethod() {
			return method;
		}
	}
}
