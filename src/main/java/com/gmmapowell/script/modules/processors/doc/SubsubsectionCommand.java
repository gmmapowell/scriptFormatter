package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class SubsubsectionCommand implements AtCommandHandler {
	private final ConfiguredState state;
	private final ScannerAtState sas;

	public SubsubsectionCommand(ScannerAtState sas) {
		this.state = sas.state();
		this.sas = sas;
	}
	
	@Override
	public String name() {
		return "Subsubsection";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.arg("title");
		if (title == null)
			throw new RuntimeException("Subsection without title");
		String anchor = cmd.arg("anchor");
		state.newPara("subsubsection-title");
		sas.outlineEntry(5, title, null, anchor);
		state.processText(title);
		state.endPara();
	}
}
