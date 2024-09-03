package com.gmmapowell.script.modules.doc.includecode;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class StopAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public StopAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "stop";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("at", "elide");
		if (!state.hasPendingCommand())
			throw new CantHappenException("&stop requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&stop can only be in &include");
		((IncludeAmp)pending.handler).includer().stopAt(params.get("at"), params.get("elide"));
	}

}
