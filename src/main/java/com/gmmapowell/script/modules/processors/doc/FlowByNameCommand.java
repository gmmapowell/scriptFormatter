package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class FlowByNameCommand implements AtCommandHandler {
	private final ConfiguredState state;

	public FlowByNameCommand(ScannerAtState sas) {
		this.state = sas.state();
	}
	
	@Override
	public String name() {
		return "FlowByName";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String fn = state.fileName();
		String flow = fn.replace(".txt","").trim();
		state.ensureFlow(flow);
		state.newSection(flow, "chapter");
	}

}
