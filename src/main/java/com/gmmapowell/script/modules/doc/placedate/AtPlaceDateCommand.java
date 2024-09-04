package com.gmmapowell.script.modules.doc.placedate;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class AtPlaceDateCommand implements AtCommandHandler {
	private final ConfiguredState sink;

	public AtPlaceDateCommand(ScannerAtState sas) {
		this.sink = sas.state();
	}

	@Override
	public String name() {
		return "PlaceDate";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String p = cmd.arg("place");
		String d = cmd.arg("date");
		sink.newSection("main", "placedate");
		sink.newSection("footnotes", "placedate");
		sink.newPara("locate-place");
		sink.processText(p);
		sink.newPara("locate-date");
		sink.processText(d);
		sink.endPara();
	}

}
