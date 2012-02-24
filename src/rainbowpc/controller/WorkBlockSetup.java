package rainbowpc.controller;

import rainbowpc.controller.ControllerMessage;

public class WorkBlockSetup extends ControllerMessage {
	private int blockSize;

	public WorkBlockSetup(int blockSize) {
		this.blockSize = blockSize;
	}

	public boolean acceptableBlockSize() {
		return getBlockSize() > 0;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public int setBlockSize() {
		return blockSize;
	}
}
