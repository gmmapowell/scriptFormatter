package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class RemoveAmp implements AmpCommandHandler {

	public RemoveAmp(ScannerAmpState state) {
		
	}
	
	@Override
	public String name() {
		return "remove";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		// TODO Auto-generated method stub
		
	}

}
