package com.gmmapowell.script.modules.processors.doc;

public class AuthorCommand implements AtCommandHandler {
	public AuthorCommand(ScannerAtState sas) {
	}
	
	@Override
	public String name() {
		return "Author";
	}

	@Override
	public void invoke(AtCommand cmd) {
		// TODO: it seems to me there never has been an implementation of this
		// Also, flas-reference uses it but it seems flas-guide doesn't
		System.out.println("author command not implemented");
	}
}
