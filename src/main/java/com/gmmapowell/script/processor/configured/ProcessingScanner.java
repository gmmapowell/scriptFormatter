package com.gmmapowell.script.processor.configured;

public interface ProcessingScanner {
	default void closeIfNotContinued(String nx) {}
	default boolean wantTrimmed() { return true; }
	boolean handleLine(String s);
	default void placeDone() {}
}
