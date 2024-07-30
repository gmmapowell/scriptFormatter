package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.util.Map;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.NonBreakingSpace;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState.ScanMode;
import com.gmmapowell.script.sink.Sink;

public abstract class AtPipeline<T extends AtState> extends ProsePipeline<T> {
	protected final ScanMode scanmode;
	protected final boolean joinspace;

	public AtPipeline(Region root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
		if (options.containsKey("scanmode")) {
			this.scanmode = ScanMode.valueOf(options.remove("scanmode").toUpperCase());
		} else
			this.scanmode = ScanMode.NONE;
		if (options.containsKey("joinspace"))
			this.joinspace = Boolean.parseBoolean(options.remove("joinspace"));
		else
			this.joinspace = false;
	}

	protected boolean commonHandleLine(T state, String s) throws IOException {
		if (s.equals("$$")) {
			state.blockquote = !state.blockquote;
			return true;
		} else if (s.startsWith("@")) {
			// it's a block starting command
			commitCurrentCommand();
			state.cmd = new DocCommand(s.substring(1));
			return true;
		} else if (state.cmd != null) {
			int pos = s.indexOf('=');
			if (pos == -1)
				throw new RuntimeException("invalid argument to " + state.cmd + ": " + s);
			state.cmd.arg(s.substring(0, pos).trim(), s.substring(pos+1).trim());
			return true;
		} else if (s.startsWith("*")) {
			int idx = s.indexOf(" ");
			if (idx == 1)
				state.newPara("bullet");
			else
				state.newPara("bullet" + idx);
			state.newSpan("bullet-sign");
			state.text("\u2022");
			state.endSpan();
			ProcessingUtils.process(state, s.substring(idx+1).trim());
			return true;
		} else if (state.blockquote && s.startsWith("|")) {
			state.newPara("blockquote");
			state.newSpan();
			int i=0;
			for (i=0;i<s.length();i++) {
				if (s.charAt(i) == '|') {
					state.op(new NonBreakingSpace());
					state.op(new NonBreakingSpace());
				} else
					break;
			}
			while (Character.isWhitespace(s.charAt(i)))
				i++;
			if (i < s.length())
				ProcessingUtils.processPart(state, s, i, s.length());
			return true;
		} else 
			return false;
	}
}
