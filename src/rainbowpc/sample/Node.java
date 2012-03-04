package rainbowpc.sample;

import rainbowpc.node.NodeProtocol;
import rainbowpc.RainbowException;
import java.io.IOException;

public class Node {
	public static void main(String[] args) {
		try {
			NodeProtocol protocol = new NodeProtocol("localhost", 7002);
		// fine, use Wesley's bracket style
		} catch (IOException e) {
			System.out.println("Could not connect to controller, bailing out");
		} catch (RainbowException e) {
		}
	}
}
