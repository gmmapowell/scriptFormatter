package com.gmmapowell.script.modules.doc.scanmode;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class NumberingCommand implements AtCommandHandler {
	private final ScanmodeState state;

	public NumberingCommand(ScannerAtState sas) {
		this.state = sas.global().requireState(ScanmodeState.class);
	}

	@Override
	public String name() {
		return "Numbering";
	}

	@Override
	public void invoke(AtCommand cmd) {
		state.pushNumbering("arabic", 1);
	}
	
	@Override
	public void onEnd(AtCommand cmd) {
		state.popNumbering();
	}
}
