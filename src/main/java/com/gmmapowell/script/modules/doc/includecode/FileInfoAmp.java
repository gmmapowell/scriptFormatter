package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class FileInfoAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public FileInfoAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "fileinfo";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		String info = cmd.args.readArg();
		state.newPara("fileinfo");
		state.newSpan();
		state.text(info);
		state.endPara();

	}

}
