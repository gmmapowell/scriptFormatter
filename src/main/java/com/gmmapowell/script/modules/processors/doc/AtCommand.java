package com.gmmapowell.script.modules.processors.doc;

import java.util.Map;
import java.util.TreeMap;

public class AtCommand {
	final String name;
	private final Map<String, String> args = new TreeMap<>();

	public AtCommand(String name) {
		this.name = name;
	}
	
	public void arg(String key, String value) {
		args.put(key, value);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String arg(String a) {
		return args.get(a);
	}
}
