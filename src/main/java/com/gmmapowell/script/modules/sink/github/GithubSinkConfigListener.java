package com.gmmapowell.script.modules.sink.github;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.sink.github.GithubSink;
import com.gmmapowell.script.sink.html.HTMLSink;
import com.gmmapowell.script.utils.Command;

public class GithubSinkConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public GithubSinkConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "dir": 
//		case "meta":
//		case "show":
//		case "open":
//		case "upload":
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
	public void complete() throws ConfigException {
		String dir = vars.remove("dir");
		if (dir == null)
			throw new ConfigException("output dir was not defined");
//		String meta = vars.remove("meta");
//		if (meta == null)
//			throw new ConfigException("meta file was not defined");
//		String show = vars.remove("show");
//		boolean wantShow = false;
//		if ("true".equals(show))
//			wantShow = true;
//		String upload = vars.remove("upload");
		try {
			state.config.sink(new HTMLSink(state.root, state.universe().regionPath(dir)));
			state.config.sink(new GithubSink(state.root, state.universe().regionPath(dir), state.debug));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating PresenterSink: " + ex.getMessage());
		}
	}

}
