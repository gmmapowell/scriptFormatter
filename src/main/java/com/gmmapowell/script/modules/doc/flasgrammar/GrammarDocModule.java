package com.gmmapowell.script.modules.doc.flasgrammar;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.utils.Utils;

public class GrammarDocModule implements ModuleActivator {
	private final ReadConfigState state;
	private final String grammarPlace;

	public GrammarDocModule(ReadConfigState state, VarMap vars) {
		this.state = state;
		this.grammarPlace = Utils.subenvs(vars.remove("grammar"));
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		GrammarConfig config = proc.global().requireState(GrammarConfig.class);
		config.setGrammar(state.root.placePath(grammarPlace));
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, GrammarAmp.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, RemoveOptionAmp.class);
		
		// Technically, I think this should be in a different module
		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, OptionCommand.class);
	}
}
