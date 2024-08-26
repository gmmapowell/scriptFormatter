package com.gmmapowell.script.modules.processors.doc;

public class SubsectionCommand implements AtCommandHandler {

	public SubsectionCommand(ScannerAtState state) {
		
	}
	
	@Override
	public String name() {
		return "Subsection";
	}

	@Override
	public void invoke(AtCommand cmd) {
	}
}
