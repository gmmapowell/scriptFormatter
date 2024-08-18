package com.gmmapowell.script.config.reader;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

public class CreateLoader implements ConfigListener {
	private final ReadConfigState state;

	public CreateLoader(ReadConfigState state, LineArgsParser lap) {
		this.state = state;
	}

	@Override
	public ConfigListener dispatch(Command cmd) {
		throw new NotImplementedException();
	}

	@Override
	public void complete() {
		throw new NotImplementedException();
	}
}
