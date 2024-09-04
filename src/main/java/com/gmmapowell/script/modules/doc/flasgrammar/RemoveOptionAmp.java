package com.gmmapowell.script.modules.doc.flasgrammar;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class RemoveOptionAmp implements AmpCommandHandler {

	public RemoveOptionAmp(ScannerAmpState state) {
//		global = state.global();
//		this.state = state.state();
//		ic = global.requireState(IncluderConfig.class);
//		samples = ic.samples();
	}
	
	@Override
	public String name() {
		return "removeOption";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		System.out.println("must implement &removeOption");
	}

}
