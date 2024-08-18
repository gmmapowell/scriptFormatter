package com.gmmapowell.script;

@SuppressWarnings("serial")
public class ScriptFormatterHelpException extends Exception {
	private final String msg;
	
	public ScriptFormatterHelpException() {
		this(null);
	}

	public ScriptFormatterHelpException(String msg) {
		this.msg = msg;
	}

	public void help() {
		System.out.println("Usage: ScriptFormatter <config>");
		if (msg != null)
			System.out.println("  " + msg);
	}

}
