package com.gmmapowell.script.modules.git;

import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;

public class GitInstaller {
	private final ReadConfigState state;

	public GitInstaller(ReadConfigState state) {
		this.state = state;
	}

	public void activate(ProcessorConfig proc) {
		GitState git = proc.global().requireState(GitState.class);
		git.formatterRoot(state.root);
		proc.addExtension(AmpCommandHandler.class, GitRootAmp.class);
		proc.addExtension(AmpCommandHandler.class, GitImportAmp.class);
	}

}
