package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class BoldAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public BoldAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "bold";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (cmd.args.toString().trim().length() > 0) {
			throw new CantHappenException("&bold must introduce and end bold mode on its own");
		}
		if (state.hasFormat("bold")) {
			state.popFormat("bold");
		} else {
			state.pushFormat("bold");
		}
	}
}
