package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.utils.LineArgsParser;

public class AmpCommand {
	final String name;
	final LineArgsParser args;

	public AmpCommand(String name, LineArgsParser args) {
		this.name = name;
		this.args = args;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
