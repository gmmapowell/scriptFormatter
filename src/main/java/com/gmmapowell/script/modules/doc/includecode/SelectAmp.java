package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class SelectAmp implements AmpCommandHandler {

	public SelectAmp(ScannerAmpState state) {
		
	}
	
	@Override
	public String name() {
		return "select";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		// TODO Auto-generated method stub
		
	}

}
