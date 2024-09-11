package com.gmmapowell.script.modules.processors.presenter;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.utils.Command;

public class PresenterProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private List<ModuleConfigListener> modules = new ArrayList<>();
	private String imagedir;

	public PresenterProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "module": {
			ModuleConfigListener nmc = state.module(cmd.line().readArg());
			modules.add(nmc);
			return nmc;
		}
		case "imagedir": 
		{
			this.imagedir = cmd.line().readArg();
			return null;
		}
		default: {
			throw new NotImplementedException("presenter processor does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws ConfigException {
		if (imagedir == null)
			throw new ConfigException("must specify imagedir");
		try {
			GlobalState global = state.config.newGlobalState();
			PresenterGlobals mgl = global.requireState(PresenterGlobals.class);
			mgl.configure(state.root, global.flows(), imagedir);

			ConfiguredProcessor proc = new ConfiguredProcessor(global, state.root, new BlockishElementFactory(), null, state.debug);
			proc.setDefaultHandler(IgnoreLine.class);
			proc.setBlankHandler(IgnoreLine.class);

			// All the important things start with "*"
			proc.addScanner(StarScanner.class);

			proc.lifecycleObserver(new PresenterLifecycle());
			state.config.processor(proc);
			for (ModuleConfigListener m : modules) {
				m.activate(proc);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating PresenterProcessor: " + ex.getMessage());
		}
	}
}
