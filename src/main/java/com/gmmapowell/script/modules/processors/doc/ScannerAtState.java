package com.gmmapowell.script.modules.processors.doc;

public class ScannerAtState {
	private AtCommand cmd;
	
	public void startCommand(String cmd) {
		this.cmd = new AtCommand(cmd);
	}

	public boolean wantFields() {
		return cmd != null;
	}

	public void cmdField(String key, String value) {
		this.cmd.arg(key, value);
	}

	public void handleAtCommand() {
		System.out.print("[need to copy commitCurrentCommand here]...");
		this.cmd = null;
	}
}
