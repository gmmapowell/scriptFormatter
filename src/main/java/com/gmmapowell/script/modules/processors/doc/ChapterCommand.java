package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ChapterCommand implements AtCommandHandler {
	private final ScannerAtState sas;
	private final ConfiguredState state;

	public ChapterCommand(ScannerAtState sas) {
		this.sas = sas;
		this.state = sas.state();
	}
	
	@Override
	public String name() {
		return "Chapter";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.args.get("title");
		if (title == null)
			throw new RuntimeException("Chapter without title");
		String style = cmd.args.get("style");
		if (style == null)
			style = "chapter";
		String anchor = cmd.args.get("anchor");

		System.out.println("chapter " + title + ": " + style);
		
		state.newSection("footnotes", style);
		state.newSection("main", style);
		state.newPara("chapter-title");
		sas.outlineEntry(1, title, style, anchor);
		state.processText(title);
		state.endPara();
	}
}
