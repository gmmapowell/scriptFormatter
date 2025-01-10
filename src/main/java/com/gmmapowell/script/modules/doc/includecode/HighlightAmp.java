package com.gmmapowell.script.modules.doc.includecode;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class HighlightAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public HighlightAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "highlight";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("matching", "from", "to", "lines");
		if (!state.hasPendingCommand())
			throw new CantHappenException("&highlight requires active &include");
		AmpCommand pending = state.pendingCommand();
		if (!(pending.handler instanceof IncludeAmp))
			throw new CantHappenException("&highlight can only be in &include");
		if (params.containsKey("matching"))
			((IncludeAmp)pending.handler).includer().highlightMatching(params.get("matching"));
		else if (params.containsKey("from")) {
			String from=params.get("from");
			if (params.containsKey("lines")) {
				int lines = Integer.parseInt(params.get("lines"));
				((IncludeAmp)pending.handler).includer().highlightRange(from, lines);
			} else {
				throw new CantHappenException("&highlight from needs to or lines");
			}
				
		}
			
	}

}
