package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.flow.AnchorOp;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class TOCOutline implements DocumentOutline {
	private final ConfiguredState sink;
	private final TOCState state;

	public TOCOutline(ScannerAtState sas) {
		this.sink = sas.state();
		state = sas.global().requireState(TOCState.class);
	}

	@Override
	public void entry(int level, String tocFormat, String title, String anchor) {
		if (tocFormat != null && tocFormat.length() > 0) {
			TOCEntry entry = state.toc().heading(level, anchor, tocFormat, title);
			sink.newSpan();
			sink.op(new AnchorOp(entry));
			sink.processText(tocFormat);
			sink.processText(" ");
		}
	}
}
