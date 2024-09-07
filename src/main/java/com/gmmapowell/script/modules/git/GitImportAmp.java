package com.gmmapowell.script.modules.git;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class GitImportAmp implements AmpCommandHandler {
	private final GitState gitstate;
	private final ConfiguredState sink;

	public GitImportAmp(ScannerAmpState state) {
		sink = state.state();
		gitstate = state.global().requireState(GitState.class);
	}
	
	@Override
	public String name() {
		return "import";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		String branch = cmd.args.readString();
		String filespec = cmd.args.readString();
		String from = null, to = null;
		if (cmd.args.hasMore()) {
			from = cmd.args.readString();
		}
		if (cmd.args.hasMore()) {
			to = cmd.args.readString();
		}
		gitstate.showDelta(sink, branch, filespec, from, to);
	}

}
