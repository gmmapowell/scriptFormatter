package com.gmmapowell.script.processor.prose;

import java.util.Map;
import java.util.TreeMap;

public class DocCommand {
	final String name;
	final Map<String, String> args = new TreeMap<>();

	public DocCommand(String name) {
		this.name = name;
	}
	
	public void arg(String key, String value) {
		args.put(key, value);
	}

	@Override
	public String toString() {
		return this.name;
	}
}
