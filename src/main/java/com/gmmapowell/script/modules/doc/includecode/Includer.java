package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;

public class Includer implements ModuleActivator {
	private final ReadConfigState state;

	public Includer(ReadConfigState state, VarMap vars) {
		this.state = state;
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, IncludeAmp.class);
	}
}
