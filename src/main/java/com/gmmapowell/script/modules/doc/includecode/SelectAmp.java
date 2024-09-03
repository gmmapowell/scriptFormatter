package com.gmmapowell.script.modules.doc.includecode;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class SelectAmp implements AmpCommandHandler {
	private ScannerAmpState state;

	public SelectAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "select";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.hasPendingCommand())
			throw new CantHappenException("&remove requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&remove can only be in &include");
		((IncludeAmp)pending.handler).select(this);
		
	}

}
