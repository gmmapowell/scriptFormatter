package com.gmmapowell.script.processor.configured;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.modules.processors.doc.GlobalState;

public interface LifecycleObserver {
	default void newPlace(ConfiguredState state, Place x) {}
	default void placeDone(ConfiguredState state) {}
	default void processingDone() {}
	default void allDone(GlobalState state) {}
}
