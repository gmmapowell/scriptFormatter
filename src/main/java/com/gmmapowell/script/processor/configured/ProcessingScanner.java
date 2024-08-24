package com.gmmapowell.script.processor.configured;

public interface ProcessingScanner {
	default void closeIfNotContinued(String nx) {}
	boolean handleLine(String s);
	default void placeDone() {}
}
