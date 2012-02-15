package rainbowpc;

import com.google.gson.Gson;

public class Message {
	private static final int version = 1;
	private static transient final Gson translator = new Gson();
	protected String method = null;
	protected String type = null;

	public Message() {
	}

	public String jsonEncode() {
		return translator.toJson(this);
	}
}	
