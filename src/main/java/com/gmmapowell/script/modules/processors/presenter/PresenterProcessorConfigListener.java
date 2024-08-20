package com.gmmapowell.script.modules.processors.presenter;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.NestedModuleCreator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.processor.movie.MoviePipeline;
import com.gmmapowell.script.processor.presenter.PresenterPipeline;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.Command;

public class PresenterProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();
	private List<ModuleConfigListener> modules = new ArrayList<>();

	public PresenterProcessorConfigListener(ReadConfigState state) {
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
		case "imagedir": 
//		case "dramatis":
		{
			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
			return null;
		}
		default: {
			throw new NotImplementedException("presenter processor does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws ConfigException {
			
//		String creds = vars.remove("credentials");
//		if (creds == null)
//			throw new ConfigException("credentials was not defined");
//		String blogUrl = vars.remove("blogurl");
//		if (blogUrl == null)
//			throw new ConfigException("blogurl was not defined");
//		String posts = vars.remove("posts");
//		if (posts == null)
//			throw new ConfigException("posts was not defined");
//		boolean localOnly = false;
//		String lo = vars.remove("local");
//		if (lo != null && "true".equalsIgnoreCase(lo))
//			localOnly = true;
//		Place saveContentAs = null;
//		String sca = vars.remove("saveAs");
//		if (sca != null)
//			saveContentAs = state.root.place(sca);
//		Place pf = state.root.placePath(posts);
//		Place cp = state.root.placePath(creds);
		try {
			Sink sink = state.config.makeSink();
			PresenterPipeline proc = new PresenterPipeline(state.root, new BlockishElementFactory(), sink, vars, state.debug);
			state.config.processor(proc);
			for (ModuleConfigListener m : modules) {
				m.activate(proc);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
		}
	}
}
