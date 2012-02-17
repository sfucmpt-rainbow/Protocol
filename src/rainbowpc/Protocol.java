package rainbowpc;

import com.google.gson.Gson;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import rainbowpc.Message;

public class Protocol implements Runnable {
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
	protected Gson translator = new Gson(); 
	protected ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	protected final Semaphore queueLock = new Semaphore(MUTEX);
	
	/**
	  * RPC mapping
	  */
	protected static final Map<String, Class> rpcMap;
	static {
		Map<String, Class> builder = new HashMap<String, Class>();
		rpcMap = Collections.unmodifiableMap(builder);
	}

	///////////////////////////////////////////////////////////
	// Constructors
	//
	public Protocol(String host) throws IOException {
		this(host, DEFAULT_PORT);
	}

	public Protocol(String host, int port) throws IOException {
		this(new Socket(host, port));
	}		

	public Protocol(Socket socket) throws IOException {
		this.initBuffers(socket);
	}

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
					Class messageType = rpcMap.get(header.getMethod());
					if (messageType != null) {
						queueMessage(data, messageType);
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
	protected String sendMessage(Message msg) throws IOException {
		return this.sendMessage(msg, false);
	}

	protected String sendMessage(Message msg, boolean waitForResponse) throws IOException {
		String result = null;
		String payload = buildPayload(VERSION, msg.getType(), msg.jsonEncode());
		outstream.println(payload);
		if (waitForResponse) {
			result = instream.readLine();
		}
		return result;
	}

	protected String buildPayload(int version, String methodType, String data) {
		return version + "|" + methodType + "\n" + data;
	}

	@SuppressWarnings("unchecked")
	private void queueMessage(String rawJson, Class type) {
		Object msg = translator.fromJson(rawJson, type);
		if (msg instanceof Message) {
			messageQueue.add((Message)msg);
		}
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
