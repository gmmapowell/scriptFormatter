package com.gmmapowell.script.modules.processors.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.doc.toc.TOCState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ScannerAtState {
	private AtCommand cmd;
	private Map<String, AtCommandHandler> handlers;
	private Set<DocumentOutline> outline;
	private ConfiguredState state;
	private int nextFnText = 1;
	private List<EndDispatcher> cmdstack = new ArrayList<>();
	private OutlineNumbering olstate;

	public void configure(ConfiguredState state) {
		this.state = state;
		this.handlers = state.extensions().forPointByName(AtCommandHandler.class, this);
		this.outline = state.extensions().forPoint(DocumentOutline.class, this);
		this.olstate = state.global().existingState(TOCState.class).toc.outlineNumbering;
	}
	
	public ConfiguredState state() {
		return state;
	}

	public GlobalState global() {
		return state.global();
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
		while (!cmdstack.isEmpty()) {
			EndDispatcher c0 = cmdstack.get(0);
			if (!c0.handler.canContain(handler))
				popAtCommand();
			else
				break;
		}
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
	
	public void outlineEntry(int level, String tocFormat, String text, String style, String anchor) {
		olstate.parseFormats(tocFormat);
		for (DocumentOutline e : outline) {
			e.entry(level, olstate.format(level, text, style, anchor), text, anchor);
		}
	}
}
