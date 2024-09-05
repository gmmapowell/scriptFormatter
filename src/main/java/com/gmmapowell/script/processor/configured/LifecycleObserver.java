package com.gmmapowell.script.processor.configured;

public interface LifecycleObserver {
	default void processingDone() {}
	default void allDone() {}
}
