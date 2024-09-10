package com.gmmapowell.script.modules.processors.movie;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class MovieLifecycle implements LifecycleObserver {

	@Override
	public void newPlace(ConfiguredState state, Place x) {
		try {
			MovieMode mode = state.require(MovieMode.class);
			mode.configure(state);
			state.newSection("main", "section");
			String getTitle = state.global().requireState(MovieGlobals.class).extractTitle();
			if (getTitle != null) {
				mode.showTitle(getTitle);
			}
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
	
	@Override
	public void placeDone(ConfiguredState state) {
		try {
			MovieMode mode = state.require(MovieMode.class);
			mode.flush();
			mode.fileDone();
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
