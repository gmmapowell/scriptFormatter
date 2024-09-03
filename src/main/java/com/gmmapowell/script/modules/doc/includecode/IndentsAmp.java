package com.gmmapowell.script.modules.doc.includecode;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class IndentsAmp implements AmpCommandHandler {

	private final ScannerAmpState state;

	public IndentsAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "indents";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("from", "to");
		if (!state.hasPendingCommand())
			throw new CantHappenException("&indents requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&indents can only be in &include");
		((IncludeAmp)pending.handler).includer().indents(Integer.parseInt(params.get("from")), Integer.parseInt(params.get("to")));
	}

}
