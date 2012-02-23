package rainbowpc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import rainbowpc.Message;
import rainbowpc.RainbowFormatter;

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
	protected boolean terminated = false;
	protected Socket socket = null;             
	protected BufferedReader instream = null;
	protected PrintWriter outstream = null;
	protected ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	protected static final Gson translator = new Gson(); 

	/**
	  * RPC mapping
	  */
	protected Map<String, RpcAction> rpcMap;

	///////////////////////////////////////////////////////////
	// Constructors
	//
	public Protocol() throws IOException {
		this((Socket)null);
	}          // default constructor, do nothing

	public Protocol(String host) throws IOException {
		this(host, DEFAULT_PORT);
	}

	public Protocol(String host, int port) throws IOException {
		this(new Socket(host, port));
	}		

	public Protocol(Socket socket) throws IOException {
		initLogger();
		log("Protocol booting...");

		if (socket != null) 
			this.initBuffers(socket);

		initRpcMap();
		log("Protocol finished booting!");
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

	private void initLogger() {
		for (Handler handler : logger.getParent().getHandlers()) {
			logger.getParent().removeHandler(handler);
		}
		ConsoleHandler consoleHandle = new ConsoleHandler();
		consoleHandle.setFormatter(new RainbowFormatter());
		logger.addHandler(consoleHandle);
	}

	///////////////////////////////////////////////////////////
	// Object class helpers
	//
	protected void log(String msg) {
		logger.info(msg);
	}

	protected void warn(String msg) {
		logger.warning(msg);
	}

	///////////////////////////////////////////////////////////
	// Main Task: Sucks up messages and queues them
	//
	public void run() {
		while(!terminated) {
			try {
				receiveMessage();
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("I/O exception occured in protocol, shutting down.");
				shutdown();
			}
		}
	}

	private void receiveMessage() throws IOException {
		Header header = new Header(instream.readLine());
		String data = instream.readLine();
		log("A message has been received");
		if (header.isAcceptedVersion()) {
			parseMessage(header, data);
		}
		else {
			log("Message dropped due to invalid header version");
		}
	}

	private void parseMessage(Header header, String data) {
		log("Message has been accepted");
		RpcAction rpcAction = rpcMap.get(header.getMethod());
		if (rpcAction != null) {
			rpcAction.run(data);
		}
		else {
			warn("Undefined rpc action for " + 
				header.getMethod() + 
				" in class " + 
				this.getClass().getName()
			);
		}
	}

	///////////////////////////////////////////////////////////
	// Message handling methods
	//
	protected void sendMessage(String method, Message msg) throws IOException {
		String payload = buildPayload(VERSION, method, translator.toJson(msg));
		outstream.println(payload);
	}

	public Message getMessage() {
		if (hasMessages()) {
			return messageQueue.poll();
		}
		return null;
	}

	public boolean hasMessages() {
		return !messageQueue.isEmpty();
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
		terminated = true;
	}

	public boolean isAlive() {
		return this.socket.isConnected();
	}

	protected class Header {
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
	public interface Protocolet extends Runnable, Comparable<Protocolet> {
		public void sendMessage(String method, Message msg) throws IOException;
		public Message getMessage();
		
		public int queueSize();
		
		public String getId();
	}
}
