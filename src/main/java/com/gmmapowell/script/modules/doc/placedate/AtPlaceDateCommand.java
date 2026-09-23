package com.gmmapowell.script.modules.doc.placedate;

import java.util.Set;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class AtPlaceDateCommand implements AtCommandHandler {
	private final ConfiguredState sink;
	private Set<DocumentOutline> docoutline;
	private String tocFormat;

	public AtPlaceDateCommand(ScannerAtState sas) {
		this.sink = sas.state();
		this.docoutline = sas.global().extensions().forPoint(DocumentOutline.class, sas);
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
		for (DocumentOutline dol : docoutline) {
			dol.entry(18, tocFormat, p + " " + d, null, null);
		}
		sink.newSection("footnotes", "placedate");
		sink.newSection("main", "placedate");
		sink.newPara("locate-place");
		sink.processText(p);
		sink.newPara("locate-date");
		sink.processText(d);
		sink.endPara();
	}

}
