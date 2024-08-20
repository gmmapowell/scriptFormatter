package com.gmmapowell.script.modules.output.html;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.sink.html.HTMLSink;
import com.gmmapowell.script.utils.Command;

public class HTMLOutputConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public HTMLOutputConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "store": 
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
		String storeInto = vars.remove("store");
		if (storeInto == null)
			throw new ConfigException("store directory was not defined");

		try {
			state.config.sink(new HTMLSink(state.root, storeInto));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating EPubSink: " + ex.getMessage());
		}
	}

}
