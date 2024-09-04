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
		return "Subsection";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.args.get("title");
		if (title == null)
			throw new RuntimeException("Subsection without title");
		String anchor = cmd.args.get("anchor");
		state.newPara("subsubsection-title");
		sas.outlineEntry(5, title, null, anchor);
		state.processText(title);
		state.endPara();
	}
}
