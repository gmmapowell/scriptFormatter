package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.TextState;

public class BoringFormatter implements Formatter {
	private final TextState state;

	public BoringFormatter(TextState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		state.newPara(state.formatAs());
		ProcessingUtils.process(state, text.replace("\t", "    "));
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
