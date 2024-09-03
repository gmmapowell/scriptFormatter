package com.gmmapowell.script.modules.processors.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

public class ScannerAmpState {
	private ConfiguredState state;
	private List<AmpCommand> stack = new ArrayList<>();
	private Map<String, AmpCommandHandler> handlers;
	
	public void configure(ConfiguredState state, ExtensionPointRepo extensions) {
		this.state = state;
		this.handlers = extensions.forPointByName(AmpCommandHandler.class, this);
	}
	
	public ConfiguredState state() {
		return state;
	}
	
	public void startCommand(String cmd, LineArgsParser lap) {
		AmpCommandHandler handler = handlers.get(cmd);
		if (handler == null)
			throw new CantHappenException("there is no handler for " + cmd + " at " + state.inputLocation());
		AmpCommand ac = new AmpCommand(handler, cmd, lap);
		stack.add(0, ac);
	}

	public boolean hasPendingCommand() {
		return !stack.isEmpty();
	}

	public void handleAmpCommand() {
		AmpCommand ac = stack.remove(0);
		ac.handler.invoke(ac);
	}

	public boolean continueCommand(Command cont, LineArgsParser lap) {
		return stack.get(0).handler.continuation(cont, lap);
	}

	public String inputLocation() {
		return state.inputLocation();
	}

	public GlobalState global() {
		return state.global();
	}

	public AmpCommand pendingCommand() {
		if (stack.isEmpty())
			throw new CantHappenException("pending command is null");
		return stack.get(0);
	}
}
