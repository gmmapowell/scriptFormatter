package com.gmmapowell.script.modules.loaders.google;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class GoogleDriveConfigListener implements ConfigListener {
	private VarMap vars = new VarMap();
	public GoogleDriveConfigListener(ReadConfigState state) {
		
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "credentials": 
		case "folder":
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
	public void complete() {
		if (index == null) {
			System.out.println(wline + ": must specify index before loader");
			return false;
		}
		config.handleLoader(vars, type, index, workdir, debug);
	}

}
