package com.gmmapowell.script.modules.doc.github;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class CopyFile implements LifecycleObserver {
//	private GHGlobal global;
	private Region storeInputs;

	public CopyFile(Region r) {
		this.storeInputs = r;
	}

	@Override
	public void newPlace(ConfiguredState state, Place x) {
//		global = state.global().existingState(GHGlobal.class);
//		WOLState wols = state.require(WOLState.class);
//		String name = x.name().replace(".txt", "");
//		System.out.println("have place " + x.name() + " to copy to " + this.storeInputs);
		Place p = this.storeInputs.ensurePlace(x.name());
		x.copyTo(p);
//		wols.currentFile(state, global, name);
	}
	
	@Override
	public void placeDone(ConfiguredState state) {
//		state.ensurePara();
//		state.newSpan();
//		state.op(new RedirectOp());
//		WOLState wols = state.require(WOLState.class);
//		wols.summarizeFile();
//		wols.outputHTML(state);
	}
	
	@Override
	public void processingDone() {
//		global.summarize();
	}

}
