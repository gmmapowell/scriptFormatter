package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class CSSFormatter implements Formatter {
	private final ConfiguredState state;

	public CSSFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent, boolean withHighlight) {
		text = text.replace("\t", "    ");
		if (exdent > 0)
			text = text.substring(exdent);
		state.newPara(withHighlight?"highlight":null);
		if (text.length() > 0) {
			if (!Character.isWhitespace(text.charAt(0))) { // an introduction line
				int idx = text.indexOf('{');
				if (idx != -1) {
					state.newSpan("bold");
					state.text(text.substring(0, idx));
					state.endSpan();
					state.newSpan();
					state.text(text.substring(idx));
				} else {
					state.newSpan();
					state.text(text);
				}
			} else {
				int idx = text.indexOf(':');
				if (idx != -1) {
					state.newSpan("bold");
					state.text(text.substring(0, idx));
					state.endSpan();
					state.newSpan();
					state.text(text.substring(idx));
				} else {
					state.newSpan();
					state.text(text);
				}
			}
		} else {
			state.newSpan();
			state.text(text);
		}
			
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
