package com.gmmapowell.script.modules.processors.doc;

public class TitleCommand implements AtCommandHandler {
	public TitleCommand(ScannerAtState sas) {
	}
	
	@Override
	public String name() {
		return "Title";
	}

	@Override
	public void invoke(AtCommand cmd) {
		// TODO: it seems to me there never has been an implementation of this
		// Also, flas-reference uses it but it seems flas-guide doesn't
		System.out.println("title command not implemented");
	}
}
