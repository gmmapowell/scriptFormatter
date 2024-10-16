package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class TOCObserver implements LifecycleObserver {
	private final TOCState toc;

	public TOCObserver(TOCState toc) {
		this.toc = toc;
	}

	@Override
	public void allDone(GlobalState state) {
		System.out.println("saving toc");
		toc.save();
	}
}
