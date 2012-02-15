package rainbowpc;

import com.google.gson.Gson;
import java.util.Queue;
import java.util.LinkedList;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import rainbowpc.Message;

public class Protocol {
	private Socket socket = null;
	private BufferedReader instream = null;
	private PrintWriter outstream = null;
	private Gson translator = new Gson();

	public Protocol(Socket socket) throws IOException {
		this.socket = socket;
		this.instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.outstream = new PrintWriter(socket.getOutputStream(), true);
	}

	protected String sendMessage(Message msg) throws IOException {
		return this.sendMessage(msg, false);
	}

	protected String sendMessage(Message msg, boolean waitForResponse) throws IOException {
		String result = null;
		String encoded = translator.toJson(msg);
		outstream.println(encoded);
		if (waitForResponse) {
			result = instream.readLine();
		}
		return result;
	}
}
