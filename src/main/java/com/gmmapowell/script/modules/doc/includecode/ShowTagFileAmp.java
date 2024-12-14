package com.gmmapowell.script.modules.doc.includecode;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class ShowTagFileAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public ShowTagFileAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "showtagfile";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.hasPendingCommand())
			throw new CantHappenException("&showtagfile requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&showtagfile can only be in &include");
		((IncludeAmp)pending.handler).includer().showtagfile(true, true);
	}

}
