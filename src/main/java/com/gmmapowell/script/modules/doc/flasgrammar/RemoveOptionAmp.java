package com.gmmapowell.script.modules.doc.flasgrammar;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class RemoveOptionAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public RemoveOptionAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "removeOption";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("prod");
		if (!state.hasPendingCommand())
			throw new CantHappenException("&removeOption requires active &grammar");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof GrammarAmp))
			throw new CantHappenException("&removeOption can only be in &grammar");
		((GrammarAmp)pending.handler).grammar().removeProd(params.get("prod"));
	}

}
