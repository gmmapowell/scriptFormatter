package com.gmmapowell.script.modules;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.movie.MovieProcessorConfigListener;
import com.gmmapowell.script.utils.Command;

public class InstallMovieModule implements ConfigListener {
	private final ReadConfigState state;
	private final ScriptConfig config;

	public InstallMovieModule(ReadConfigState state) {
		this.state = state;
		this.config = state.config;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		throw new ConfigException("InstallMovieModule cannot be configured right now");
	}

	@Override
	public void complete() throws Exception {
		state.registerProcessor("movie", MovieProcessorConfigListener.class);

//		installInlineCommands();
	}

	// & commands that appear in the line rather than at the start
	private void installInlineCommands() {
//		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, FootnoteNumHandler.class);
//		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, ForceSpaceHandler.class);
//		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, SupAmp.class);
	}

}
