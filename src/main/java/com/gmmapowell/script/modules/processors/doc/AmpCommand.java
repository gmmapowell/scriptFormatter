package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.utils.LineArgsParser;

public class AmpCommand {
	public final AmpCommandHandler handler;
	final String name;
	public final LineArgsParser args;

	public AmpCommand(AmpCommandHandler handler, String name, LineArgsParser args) {
		this.handler = handler;
		this.name = name;
		this.args = args;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
