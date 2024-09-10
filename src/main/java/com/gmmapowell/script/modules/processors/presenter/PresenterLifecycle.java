package com.gmmapowell.script.modules.processors.presenter;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class PresenterLifecycle implements LifecycleObserver {
	@Override
	public void newPlace(ConfiguredState state, Place x) {
		PresenterGlobals gl = state.global().requireState(PresenterGlobals.class);
		gl.newPlace(x);
	}

	@Override
	public void placeDone(ConfiguredState state) {
		PresenterGlobals gl = state.global().requireState(PresenterGlobals.class);
		gl.placeDone();
	}
	
	@Override
	public void allDone(GlobalState state) {
		PresenterGlobals gl = state.requireState(PresenterGlobals.class);
		gl.allDone();
	}
}
