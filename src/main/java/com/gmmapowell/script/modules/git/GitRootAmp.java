package com.gmmapowell.script.modules.git;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class GitRootAmp implements AmpCommandHandler {
	private final GitState gitstate;

	public GitRootAmp(ScannerAmpState state) {
		gitstate = state.global().requireState(GitState.class);
	}
	
	@Override
	public String name() {
		return "git";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		gitstate.repository(cmd.args.readString());
	}

}
