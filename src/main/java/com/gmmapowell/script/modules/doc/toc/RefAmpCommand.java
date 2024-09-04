package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class RefAmpCommand implements AmpCommandHandler {

	public RefAmpCommand(ScannerAmpState sas) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String name() {
		return "ref";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		System.out.println("ref command is not implemented");
	}

}
