package com.gmmapowell.script.modules.processors.blog;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class BloggerLifecycleObserver implements LifecycleObserver {
	public BloggerLifecycleObserver(ConfiguredProcessor proc) {
	}

	@Override
	public void newPlace(ConfiguredState state, Place x) {
		System.out.println("configure for place " + x);
		String file = x.name().replace(".txt", "");
		state.global().flows().flow(file);
		state.newSection(file, "blog");
	}
}
