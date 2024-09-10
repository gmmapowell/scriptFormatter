package com.gmmapowell.script.modules.processors.movie;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.NestedModuleCreator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.utils.Command;

public class MovieProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private List<ModuleConfigListener> modules = new ArrayList<>();
	private String title;
	private String dramatis;

	public MovieProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "module": {
			ModuleConfigListener nmc = new NestedModuleCreator(state).module(cmd.line().readArg());
			modules.add(nmc);
			return nmc;
		}
		case "title": {
			this.title = cmd.line().readArg();
			return null;
		}
		case "dramatis":
		{
			this.dramatis = cmd.line().readArg();
			return null;
		}
		default: {
			throw new NotImplementedException("movie processor does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws ConfigException {
		if (title == null)
			throw new ConfigException("must specify title");
		if (dramatis == null)
			throw new ConfigException("must specify dramatis");
		try {
			GlobalState global = state.config.newGlobalState();
			global.flows().flow("main");
			MovieGlobals mgl = global.requireState(MovieGlobals.class);
			mgl.configure(state.root, dramatis, title);
			ConfiguredProcessor proc = new ConfiguredProcessor(global, state.root, new BlockishElementFactory(), null, state.debug);
			proc.setDefaultHandler(LineCollector.class);
			proc.setBlankHandler(FlushProcessor.class);
			proc.addScanner(CommentIgnorer.class);
			
			proc.addScanner(SlugHandler.class);
			// fairly sure this always wants to be TOP
			proc.addScanner(SlugSpotter.class);
			
			proc.lifecycleObserver(new MovieLifecycle());
			state.config.processor(proc);
			for (ModuleConfigListener m : modules) {
				m.activate(proc);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating MovieProcessor: " + ex.getMessage());
		}
	}
}
