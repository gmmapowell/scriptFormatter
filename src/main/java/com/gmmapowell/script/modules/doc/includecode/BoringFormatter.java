package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class BoringFormatter implements Formatter {
	private final ConfiguredState state;

	public BoringFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent, boolean withHighlight) {
		state.newPara();
		state.processText(text.replace("\t", "    "));
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
