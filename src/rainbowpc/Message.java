package rainbowpc;

import com.google.gson.Gson;

public abstract class Message {
	protected String type = null;
	protected transient String method = null;
	
	public Message(String type, String method) {
		setType(type);
		setMethod(method);
	}

	protected void setType(String type) {
		this.type = type;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getMethod() {
		return method;
	}
}
