package com.gmmapowell.script.modules.doc.includecode;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class IncludeCodeConfigListener implements ModuleConfigListener {
	private final ReadConfigState state;
	private final VarMap vars = new VarMap();
	private Includer includer;

	public IncludeCodeConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		case "samples": 
		{
			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
			return null;
		}
		default: {
			throw new NotImplementedException("email quoter module does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws Exception {
		includer = new Includer(state, vars);
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		includer.activate(proc);
	}
}
