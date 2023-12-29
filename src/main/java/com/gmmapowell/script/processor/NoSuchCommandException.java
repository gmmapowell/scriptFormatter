package com.gmmapowell.script.processor;

@SuppressWarnings("serial")
public class NoSuchCommandException extends ParsingException {
	private String cmd;
	private String inputLocation;

	public NoSuchCommandException(String cmd, String inputLocation) {
		this(cmd, inputLocation, null);
	}
	
	public NoSuchCommandException(String cmd, String inputLocation, String context) {
		super("no such command: '" + cmd + "'" + (context != null ? " in " + context + " context" : "") + " at " + inputLocation);
		this.cmd = cmd;
		this.inputLocation = inputLocation;
	}

	public NoSuchCommandException context(String string) {
		return new NoSuchCommandException(cmd, inputLocation, string);
	}

}
