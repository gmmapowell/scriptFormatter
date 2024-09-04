package com.gmmapowell.script.modules.doc.placedate;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.utils.Command;

public class PlaceDateConfigListener implements ModuleConfigListener {
	private final ReadConfigState state;

	public PlaceDateConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		default: {
			throw new NotImplementedException("placedate module does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws Exception {
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, AtPlaceDateCommand.class);
	}
}
