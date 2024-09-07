package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ItalicAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public ItalicAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "italic";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (cmd.args.toString().trim().length() > 0) {
			throw new CantHappenException("&italic must introduce and end italic mode on its own");
		}
		if (state.hasFormat("italic")) {
			state.popFormat("italic");
			state.popFormat("text");
		} else {
			state.pushFormat("text");
			state.pushFormat("italic");
		}
	}
}
