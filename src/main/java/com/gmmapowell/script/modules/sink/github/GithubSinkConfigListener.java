package com.gmmapowell.script.modules.sink.github;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.doc.github.GithubModuleConfigListener;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
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
		case "pre":
		case "post":
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
		Universe u = state.universe();
		Region r = state.root;
		String dir = vars.remove("dir");
		if (dir == null)
			throw new ConfigException("output dir was not defined");
		Place prep = null, postp = null;
		String pre = vars.remove("pre");
		if (pre != null) 
			prep = r.placePath(pre);
		String post = vars.remove("post");
		if (post != null)
			postp = r.placePath(post);
		IndexPageCreator ipc = new IndexPageCreator(r.ensurePlace("index.html"));
		ModuleConfigListener m = this.state.module("github");
		((GithubModuleConfigListener)m).addLO(ipc);
//		state.config.
//		String meta = vars.remove("meta");
//		if (meta == null)
//			throw new ConfigException("meta file was not defined");
//		String show = vars.remove("show");
//		boolean wantShow = false;
//		if ("true".equals(show))
//			wantShow = true;
//		String upload = vars.remove("upload");
		try {
			HTMLSink hs = new HTMLSink(r, u.ensureRegionPath(dir), prep, postp, ipc);
			state.config.sink(hs);
			state.config.extensions().bindExtensionPoint(DocumentOutline.class, ipc);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating PresenterSink: " + ex.getMessage());
		}
	}

}
