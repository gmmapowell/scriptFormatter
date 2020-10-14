package com.gmmapowell.script.sink.pdf;

public class Suspension {
	public final Cursor cursor;
	private final StyledToken lastAccepted;

	public Suspension(Cursor cursor, StyledToken lastAccepted) {
		this.cursor = cursor;
		this.lastAccepted = lastAccepted;
	}

	public boolean isFlow(String enable) {
		return cursor.isFlow(enable);
	}
	
	@Override
	public String toString() {
		return "Supension[" + cursor + "," + lastAccepted + "]";
	}

}
