package rainbowpc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
	private static final int SOCKET_BLOCK_MILLIS = 1000; // 1 sec
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
	protected boolean exited = false;
	protected Socket socket = null;             
	protected BufferedReader instream = null;
	protected PrintWriter outstream = null;
	protected LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
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
		//this.socket.setSoTimeout(SOCKET_BLOCK_MILLIS);
	}

	protected void initLogger() {
		logger = Logger.getLogger(this.getClass().getSimpleName());
		for (Handler handler : logger.getParent().getHandlers()) {
			logger.getParent().removeHandler(handler);
		}
		ConsoleHandler consoleHandle = new ConsoleHandler();
		consoleHandle.setFormatter(new RainbowFormatter());
		if (logger.getHandlers().length < 1) 
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
			catch (SocketTimeoutException e) {}
			catch (SocketException e) {
				shutdown();
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("I/O exception occured in protocol, shutting down.");
				shutdown();
			}
		}
		log("Protocol successfully ended");
	}

	private void receiveMessage() throws IOException, SocketTimeoutException {
                String line = instream.readLine();
                if(line == null){
                    throw new SocketException("readLine was null, end of stream for socket reached");
                }
		Header header = new Header(line);
                
		String data = instream.readLine();
		log("A message has been received");
		if (header.isAcceptedHeader()) {
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
	public void sendMessage(String method, Message msg) throws IOException {
		String payload = buildPayload(VERSION, method, translator.toJson(msg));
		outstream.println(payload);
	}

	public Message getMessage() throws InterruptedException{
                return messageQueue.take();
	}
        public Message pollMessage(){
                try{
                        return messageQueue.take();
                }
                catch (InterruptedException e) {
                        Thread.currentThread().interrupted();
                        return null;
                }
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
		return version + "|" + methodType + "\n" + 
				data;
	}

	protected void queueMessage(Message msg) {
		messageQueue.add(msg);
	}
	
	///////////////////////////////////////////////////////////
	// Connection handling methods
	//
	public void shutdown() {
		log("Shutting down...");
		try {
			this.instream.close();
			this.outstream.close();
			this.socket.close();
		}
		catch (IOException e) {
			// do nothing
		}
		terminated = true;
		shutdownCallable();
		log("Terminated");
	}
	
	// allows for override hook
	protected void shutdownCallable() {
	}

	public boolean isAlive() {
		return !terminated && socket.isConnected();
	}
	
	public synchronized boolean hasExited() {
		return exited;
	}

	protected class Header {
		private byte version;
		private String method;
		private boolean deformed = false;

		public Header(String rawHeader) {
			log("Parsing header " + rawHeader);
			try {
				String[] tuple = rawHeader.split("\\|");
				version = Byte.parseByte(tuple[0]);
				method = tuple[1];
			}
			catch (Exception e) {
				deformed = true;
			}
		}
		
		public boolean isAcceptedHeader() {
			return !deformed && version <= VERSION;
		}

		public String getMethod() {
			return method;
		}
	}
	public interface Protocolet extends Runnable, Comparable<Protocolet> {
		public void sendMessage(String method, Message msg) throws IOException;
		public Message getMessage() throws InterruptedException;
		
		public int queueSize();
		
		public String getId();
		
		public String toString();
	}
}
