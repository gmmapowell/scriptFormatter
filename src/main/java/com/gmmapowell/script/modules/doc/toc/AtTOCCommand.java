package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class AtTOCCommand implements AtCommandHandler {

	public AtTOCCommand(ScannerAtState sas) {
	}

	@Override
	public String name() {
		return "TOC";
	}

	@Override
	public void invoke(AtCommand cmd) {
		// TODO Auto-generated method stub

	}

}
