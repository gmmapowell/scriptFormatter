package com.gmmapowell.script.utils;

public class NestedCommand implements Command {
	private final int nesting;
	private final String cmd;
	private final LineArgsParser line;

	public NestedCommand(int nesting, String cmd, LineArgsParser line) {
		this.nesting = nesting;
		this.cmd = cmd;
		this.line = line;
	}

	@Override
	public int depth() {
		return nesting;
	}

	@Override
	public String name() {
		return cmd;
	}

	@Override
	public LineArgsParser line() {
		return line;
	}
}
