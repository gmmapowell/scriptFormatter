package com.gmmapowell.script.modules.processors.doc;

public class CommentaryCommand implements AtCommandHandler {

	public CommentaryCommand(ScannerAtState state) {
		
	}
	
	@Override
	public String name() {
		return "Commentary";
	}

	@Override
	public void invoke(AtCommand cmd) {
	}

}
