package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.prose.Formatter;

public class BoringFormatter implements Formatter {
	private final ConfiguredState state;

	public BoringFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		state.newPara();
		state.processText(text.replace("\t", "    "));
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
