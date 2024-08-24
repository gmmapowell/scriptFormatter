package com.gmmapowell.script.modules.processors.doc;

import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.utils.LineArgsParser;

public class ScannerAmpState {
	private AmpCommand cmd;
	private Map<String, AmpCommandHandler> handlers;
	
	public void configure(ExtensionPointRepo extensions) {
		this.handlers = extensions.forPointByName(AmpCommandHandler.class, this);
	}
	
	public void startCommand(String cmd, LineArgsParser lap) {
		this.cmd = new AmpCommand(cmd, lap);
	}

	public boolean hasPendingCommand() {
		return cmd != null;
	}

	public void handleAmpCommand() {
		AmpCommandHandler handler = handlers.get(cmd.name);
		if (handler == null)
			throw new CantHappenException("there is no handler for " + cmd.name);
		handler.invoke(cmd);
		this.cmd = null;
	}
}
