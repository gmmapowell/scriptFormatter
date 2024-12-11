package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class JsonFormatter implements Formatter {
	private final ConfiguredState state;

	public JsonFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		text = text.replace("\t", "    ");
		if (exdent > 0)
			text = text.substring(exdent);
		state.newPara();
		state.newSpan();
//		int i = 0;
//		while (i < text.length()) {
//			char c = text.charAt(i);
//			i++;
//		}
		if (text.length() != 0)
			state.text(text);
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
