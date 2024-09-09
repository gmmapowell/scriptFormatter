package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.doc.includecode.IncluderConfig;
import com.gmmapowell.script.modules.git.GitState;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

public class IncludeTagAmp implements AmpCommandHandler {
	private final GitState gitstate;
	private final IncluderConfig ic;

	public IncludeTagAmp(ScannerAmpState state) {
		gitstate = state.global().requireState(GitState.class);
		ic = state.global().requireState(IncluderConfig.class);
	}
	
	@Override
	public String name() {
		return "includeTag";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (gitstate.repoPath() == null) {
			throw new CantHappenException("cannot use &includeTag before &git");
		}
		String tag = cmd.args.readString();
		String repo = "git:" + gitstate.repoPath() + ":" + tag + ":/";
		ic.setSamples(gitstate.root().regionPath(repo));
	}

}
