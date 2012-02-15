package rainbowpc;

import java.lang.Thread;
import java.lang.StackTraceElement;

public class RainbowException extends Exception {
	public String reason;
	public StackTraceElement[] trace;

	public RainbowException(String what) {
		this.reason = what;
		this.trace = Thread.currentThread().getStackTrace();
	}
}
