package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class XMLFormatter implements Formatter {
	private final ConfiguredState state;

	public XMLFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent, boolean withHighlight) {
		text = text.replace("\t", "    ");
		if (exdent > 0)
			text = text.substring(exdent);
		state.newPara(withHighlight?"highlight":null);
		state.newSpan();
		if (text.length() != 0)
			state.text(text);
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
