package com.gmmapowell.script.loader;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.gdw.GoogleDriveWorld;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class ContentLoaderConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public ContentLoaderConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "credentials": 
		case "folder":
		case "recursive":
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
		if (state.index == null) {
			throw new ConfigException(state.wline + ": must specify index before loader");
		}
		if (state.workdir == null) {
			throw new ConfigException(state.wline + ": must specify workdir before loader");
		}
		String creds = vars.remove("credentials");
		if (creds == null)
			throw new ConfigException("credentials was not defined");
		String folder = vars.remove("folder");
		if (folder == null)
			throw new ConfigException("folder was not defined");
		boolean isRecursive = false;
		String recursive = vars.remove("recursive");
		if (recursive != null && !recursive.equals("false") && !recursive.equals("no"))
			isRecursive = true;
		
		// TODO: I feel that this should be elsewhere
		// Specifically, there should be a module that loads it
		Universe universe = state.universe();
		try {
			Place credsPath = state.root.placePath(creds);
			new GoogleDriveWorld(universe, "ScriptFormatter", credsPath);
		} catch (Exception ex) {
			throw new ConfigException(ex.toString());
		}
		state.config.loader(new ContentLoader(universe, state.root, state.workdir, state.index, folder, isRecursive, state.debug));

	}

}
