package rainbowpc;

import com.google.gson.Gson;

public abstract class Message {
	protected String type = null;
	
	public Message() {
		setType();
	}

	protected abstract void setType();
}
