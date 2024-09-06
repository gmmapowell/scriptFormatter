package com.gmmapowell.script.modules.sink.blogger;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.sink.blogger.BloggerSink;
import com.gmmapowell.script.utils.Command;

public class BloggerSinkConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public BloggerSinkConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "credentials": 
		case "blogurl":
		case "posts":
		case "local":
		case "saveAs":
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
		String creds = vars.remove("credentials");
		if (creds == null)
			throw new ConfigException("credentials was not defined");
		String blogUrl = vars.remove("blogurl");
		if (blogUrl == null)
			throw new ConfigException("blogurl was not defined");
		String posts = vars.remove("posts");
		if (posts == null)
			throw new ConfigException("posts was not defined");
		boolean localOnly = false;
		String lo = vars.remove("local");
		if (lo != null && "true".equalsIgnoreCase(lo))
			localOnly = true;
		Place saveContentAs = null;
		String sca = vars.remove("saveAs");
		if (sca != null)
			saveContentAs = state.root.ensurePlace(sca);
		Place pf = state.root.placePath(posts);
		Place cp = state.root.placePath(creds);
		try {
			state.config.sink(new BloggerSink(state.root, cp, blogUrl, pf, localOnly, saveContentAs));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
		}
	}

}
