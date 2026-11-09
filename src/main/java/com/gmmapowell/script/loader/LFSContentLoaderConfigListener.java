package com.gmmapowell.script.loader;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class LFSContentLoaderConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public LFSContentLoaderConfigListener(ReadConfigState state) {
		this.state = state;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		case "recursive":
		{
			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
			return null;
		}
		default: {
			throw new NotImplementedException(cmd.name());
		}
		}
	}

	@Override
	public void complete() throws Exception {
		if (state.index == null) {
			throw new ConfigException(state.wline + ": must specify index before loader");
		}
		if (state.workdir == null) {
			throw new ConfigException(state.wline + ": must specify workdir before loader");
		}
		boolean isRecursive = false;
		String recursive = vars.remove("recursive");
		if (recursive != null && !recursive.equals("false") && !recursive.equals("no"))
			isRecursive = true;
		

		state.config.loader(new LFSContentLoader(/*universe, state.root, */ state.workdir, state.index, isRecursive, state.debug));
	}
}
