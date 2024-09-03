package com.gmmapowell.script.modules.doc.includecode;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class SelectAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public SelectAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "select";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("from", "what", "exdent");
		if (!state.hasPendingCommand())
			throw new CantHappenException("&remove requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&remove can only be in &include");
		((IncludeAmp)pending.handler).includer().selectOnly(params.get("from"), params.get("what"), params.get("exdent"));
	}

}
