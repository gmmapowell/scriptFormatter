package com.gmmapowell.script.modules.processors.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ScannerAtState {
	private AtCommand cmd;
	private Map<String, AtCommandHandler> handlers;
	private Set<DocumentOutline> outline;
	private ConfiguredState state;
	private int nextFnText = 1;
	private List<EndDispatcher> cmdstack = new ArrayList<>();

	public void configure(ConfiguredState state) {
		this.state = state;
		this.handlers = state.extensions().forPointByName(AtCommandHandler.class, this);
		this.outline = state.extensions().forPoint(DocumentOutline.class, this);
	}
	
	public ConfiguredState state() {
		return state;
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

	public void handleAtCommand() {
		AtCommandHandler handler = handlers.get(cmd.name);
		if (handler == null)
			throw new CantHappenException("there is no handler for " + cmd.name + " at " + state.inputLocation());
		handler.invoke(cmd);
		cmdstack.add(0, new EndDispatcher(handler, this.cmd));
		this.cmd = null;
	}
	
	public void popAtCommand() {
		EndDispatcher d = cmdstack.remove(0);
		d.handler.onEnd(d.cmd);
	}
	
	public int nextFootnoteText() {
		return nextFnText++;
	}

	public void ignoreNextBlanks() {
		state.ignoreNextBlanks();
	}

	public void observeBlanks() {
		state.observeBlanks();
	}
}
