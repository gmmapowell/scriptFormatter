package com.gmmapowell.script.modules.webedit;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.WebEdit;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.utils.Command;

public class WebEditConfigListener implements ConfigListener {
	private final ReadConfigState state;
	private VarMap vars = new VarMap();
	private boolean wanted = true;
	
	public WebEditConfigListener(ReadConfigState state) {
		this.state = state;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		case "file": 
		case "upload":
		case "title":
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
	public void complete() throws Exception {
		String file = vars.remove("file");
		if (file == null)
			throw new ConfigException("webedit output file was not defined");
		String upload = vars.remove("upload");
		if (upload == null)
			throw new ConfigException("upload dir was not defined");
		String title = vars.remove("title");
		if (title == null)
			throw new ConfigException("title was not defined");
		if (wanted)
			state.config.webedit(new WebEdit(state.root.place(file), upload, state.sshid, title));
	}

}
