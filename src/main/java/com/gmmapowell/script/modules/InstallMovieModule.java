package com.gmmapowell.script.modules;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.movie.MovieProcessorConfigListener;
import com.gmmapowell.script.utils.Command;

public class InstallMovieModule implements ConfigListener {
	private final ReadConfigState state;

	public InstallMovieModule(ReadConfigState state) {
		this.state = state;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		throw new ConfigException("InstallMovieModule cannot be configured right now");
	}

	@Override
	public void complete() throws Exception {
		state.registerProcessor("movie", MovieProcessorConfigListener.class);
	}
}
