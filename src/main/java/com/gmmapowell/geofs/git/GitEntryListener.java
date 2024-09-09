package com.gmmapowell.geofs.git;

@FunctionalInterface
public interface GitEntryListener {
	void entry(String type, String name);
}
