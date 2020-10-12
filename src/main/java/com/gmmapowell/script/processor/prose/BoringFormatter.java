package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.processor.ProcessingUtils;

public class BoringFormatter implements Formatter {
	private final DocState state;

	public BoringFormatter(DocState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		state.newPara("preformatted");
		ProcessingUtils.process(state, text.replace("\t", "    "));
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
