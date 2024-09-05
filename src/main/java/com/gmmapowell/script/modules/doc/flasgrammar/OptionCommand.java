package com.gmmapowell.script.modules.doc.flasgrammar;

import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class OptionCommand implements AtCommandHandler {

	public OptionCommand(ScannerAtState sas) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String name() {
		return "Option";
	}

	@Override
	public void invoke(AtCommand cmd) {
		System.out.println("we need to handle options for the Ziniki Reference Manual Appendices");
	}

}
