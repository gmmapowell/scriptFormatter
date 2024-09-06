package com.gmmapowell.script.processor.configured;

import com.gmmapowell.geofs.Place;

public interface LifecycleObserver {
	default void newPlace(ConfiguredState state, Place x) {}
	default void processingDone() {}
	default void allDone() {}
}
