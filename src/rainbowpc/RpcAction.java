package rainbowpc;

import com.google.gson.JsonElement;
import rainbowpc.Message;

public abstract class RpcAction {
	public final void run(String jsonRaw) {
		action(jsonRaw);
	}

	public abstract void action(String jsonRaw);
}
