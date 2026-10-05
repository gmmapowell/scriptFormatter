package com.gmmapowell.script.modules.doc.placedate;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class AtPlaceDateCommand implements AtCommandHandler {
	private final ConfiguredState sink;
	private String tocFormat;
	private ScannerAtState sas;

	public AtPlaceDateCommand(ScannerAtState sas) {
		this.sas = sas;
		this.sink = sas.state();
		this.tocFormat = "";
	}

	@Override
	public String name() {
		return "PlaceDate";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String p = cmd.arg("place").trim();
		String d = cmd.arg("date").trim();
		sink.newSection("footnotes", "placedate");
		sink.newSection("main", "placedate");
		sink.newPara("locate-place", "chapter-title");
		sas.outlineEntry(1, tocFormat, p + " " + d, null, null);
		sink.processText(p);
		sink.endPara();
		sink.newPara("locate-date", "chapter-title");
		sink.processText(d);
		sink.endPara();
	}
}
