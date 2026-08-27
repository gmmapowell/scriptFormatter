package com.gmmapowell.script.modules.doc.github;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;
import com.gmmapowell.script.utils.Command;

public class GithubModuleConfigListener implements ModuleConfigListener {
	private final ReadConfigState state;
	private String inputs;
	private ProcessorConfig proc;

	public GithubModuleConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		case "inputs": {
			this.inputs = cmd.line().readArg();
			cmd.line().argsDone();
			return null;
		}
		default:
			throw new CantHappenException("no such command in GitHubModule: " + cmd.name());
		}
	}
	
	@Override
	public void complete() throws Exception {
		System.out.println("Completing " + this);
	}
	
	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		System.out.println("Activating " + proc);
		proc.global().requireState(GHGlobal.class);
		Region r = state.universe().ensureRegionPath(inputs);
		proc.lifecycleObserver(new CopyFile(r));
		this.proc = proc;
	}

	public void addLO(LifecycleObserver lo) {
		this.proc.lifecycleObserver(lo);
	}
}
