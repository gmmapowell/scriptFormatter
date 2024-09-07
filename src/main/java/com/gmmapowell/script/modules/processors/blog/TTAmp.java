package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class TTAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public TTAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "tt";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (cmd.args.toString().trim().length() > 0) {
			throw new CantHappenException("&tt must introduce and end tt mode on its own");
		}
		if (state.hasFormat("preformatted")) {
			state.popFormat("preformatted");
		} else {
			state.pushFormat("preformatted");
		}
	}

}
