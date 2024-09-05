package com.gmmapowell.script.modules.doc.scanmode;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class DetailsCommand implements AtCommandHandler {
	private final ConfiguredState sink;
	private final ScanmodeState state;

	public DetailsCommand(ScannerAtState sas) {
		this.sink = sas.state();
		this.state = sas.global().requireState(ScanmodeState.class);
	}

	@Override
	public String name() {
		return "Details";
	}

	@Override
	public void invoke(AtCommand cmd) {
		if (state.scanMode(ScanMode.DETAILS))
			sink.popFormat("bold", "text");
	}

}
