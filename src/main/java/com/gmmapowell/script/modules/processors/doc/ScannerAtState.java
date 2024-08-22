package com.gmmapowell.script.modules.processors.doc;

public class ScannerAtState {
	private boolean wantFields;

	public void wantFields(boolean b) {
		this.wantFields = b;
	}

	public boolean wantFields() {
		return wantFields;
	}
}
