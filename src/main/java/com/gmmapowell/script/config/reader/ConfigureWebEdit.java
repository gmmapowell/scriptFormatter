package com.gmmapowell.script.config.reader;

import com.gmmapowell.script.modules.webedit.WebEditConfigListener;
import com.gmmapowell.script.utils.LineArgsParser;

public class ConfigureWebEdit implements ConfigListenerProvider {
	private final ReadConfigState state;

	public ConfigureWebEdit(ReadConfigState state) {
		this.state = state;
	}

	@Override
	public ConfigListener make(LineArgsParser lap) {
		return new WebEditConfigListener(state);
	}
}
