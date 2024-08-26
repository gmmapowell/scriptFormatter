package com.gmmapowell.script.modules.processors.doc;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ScannerAtState {
	private AtCommand cmd;
	private Map<String, AtCommandHandler> handlers;
	private ConfiguredState state;
	
	public void configure(ConfiguredState state) {
		this.state = state;
		this.handlers = state.extensions().forPointByName(AtCommandHandler.class, this);
	}
	
	public void startCommand(String cmd) {
		this.cmd = new AtCommand(cmd);
	}

	public boolean hasPendingCommand() {
		return cmd != null;
	}

	public void cmdField(String key, String value) {
		this.cmd.arg(key, value);
	}

	// TODO: this is where we need extension points
	// Each @Command should be its own thing
	// And should be an instanceof AtCommandHandler
	// They need to be bound somewhere in the Config
	// And we need to know to pick them up by class name
	// It's not about inheriting the class, it's about being specifically bound to it
	// And we should be able to order things so that the extension point is always defined before it is referenced
	public void handleAtCommand() {
		AtCommandHandler handler = handlers.get(cmd.name);
		if (handler == null)
			throw new CantHappenException("there is no handler for " + cmd.name);
		handler.invoke(cmd);
		this.cmd = null;
	}

	public ConfiguredState state() {
		return state;
	}
}
