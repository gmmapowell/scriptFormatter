package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class SectionCommand implements AtCommandHandler {
	private final ConfiguredState state;
	private final ScannerAtState sas;

	public SectionCommand(ScannerAtState sas) {
		this.state = sas.state();
		this.sas = sas;
	}
	
	@Override
	public String name() {
		return "Section";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.args.get("title");
		if (title == null)
			throw new RuntimeException("Section without title");
		String anchor = cmd.args.get("anchor");
		state.newPara("section-title");
		sas.outlineEntry(2, title, null, anchor);
		state.processText(title);
		state.endPara();
	}
}
