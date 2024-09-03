package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.utils.Utils;

public class Includer implements ModuleActivator {
	private final ReadConfigState state;
	private final String samplesPlace;

	public Includer(ReadConfigState state, VarMap vars) {
		this.state = state;
		this.samplesPlace = Utils.subenvs(vars.remove("samples"));
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		IncluderConfig config = proc.global().requireState(IncluderConfig.class);
		config.setSamples(state.root.regionPath(samplesPlace));
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, IncludeAmp.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, RemoveAmp.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, SelectAmp.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, IndentsAmp.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, StopAmp.class);
	}
}
