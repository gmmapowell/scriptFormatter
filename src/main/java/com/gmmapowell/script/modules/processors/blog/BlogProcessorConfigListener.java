package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.processor.prose.BlogPipeline;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.capture.CaptureSinkInFile;
import com.gmmapowell.script.utils.Command;

public class BlogProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public BlogProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
//		switch (cmd.name()) {
//		case "credentials": 
//		case "blogurl":
//		case "posts":
//		case "local":
//		case "saveAs":
//		{
//			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
//			return null;
//		}
//		default: {
			throw new NotImplementedException("blog processor does not have any parameters");
//		}
//		}
	}

	@Override
	public void complete() throws ConfigException {
		Sink sink = state.config.makeSink();
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
//		try {
			state.config.processor(new BlogPipeline(state.root, new BlockishElementFactory(), sink, vars, state.debug));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
//		}
	}
}
