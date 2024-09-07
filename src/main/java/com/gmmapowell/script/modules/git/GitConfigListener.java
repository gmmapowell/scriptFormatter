package com.gmmapowell.script.modules.git;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class GitConfigListener implements ModuleConfigListener {
	private final ReadConfigState state;
	private GitInstaller module;

	public GitConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		throw new NotImplementedException("git module does not have parameters");
	}

	@Override
	public void complete() throws Exception {
		module = new GitInstaller(state);
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		module.activate(proc);
	}

}
