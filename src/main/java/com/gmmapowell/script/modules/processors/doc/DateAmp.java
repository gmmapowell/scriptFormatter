package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class DateAmp implements AmpCommandHandler {
	private ConfiguredState sink;

	public DateAmp(ScannerAmpState state) {
		this.sink = state.state();
	}
	
	@Override
	public String name() {
		return "date";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		String dt = cmd.args.readArg().trim();
		sink.newPara("locate-date");
		sink.processText(dt);
		sink.endPara();
	}
}
