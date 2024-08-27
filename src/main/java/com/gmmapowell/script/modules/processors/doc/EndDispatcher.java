package com.gmmapowell.script.modules.processors.doc;

public class EndDispatcher {
	public final AtCommandHandler handler;
	public final AtCommand cmd;

	public EndDispatcher(AtCommandHandler handler, AtCommand cmd) {
		this.handler = handler;
		this.cmd = cmd;
	}
}
