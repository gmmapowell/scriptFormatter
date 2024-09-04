package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.prose.Formatter;

public class HTMLFormatter implements Formatter {
	private boolean inTag = false;
	private final ConfiguredState state;

	public HTMLFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		text = text.replace("\t", "    ");
		if (exdent > 0)
			text = text.substring(exdent);
		state.newPara();
		state.newSpan();
		int i = 0;
		boolean isTag = false;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (inTag) {
				if (c == '>') {
					if (isTag) {
						state.nestSpan("bold");
						state.text(text.substring(0, i));
						state.popSpan();
					} else {
						state.text(text.substring(0, i));
					}
					text = text.substring(i);
					inTag = false;
					i = 1;
				} else if (c == ' ') {
					if (isTag) {
						state.nestSpan("bold");
						state.text(text.substring(0, i));
						state.popSpan();
					} else {
						state.text(text.substring(0, i));
					}
					isTag = false;
					text = text.substring(i);
					i = 1;
				} else if (c == '=') { // it's actually a more complicated condition than this, because we can have '=' in the attribute strings
					state.nestSpan("bold");
					state.text(text.substring(0, i));
					state.popSpan();
					text = text.substring(i);
					i = 1;
				} else
					i++;
			} else if (c == '<') {
				inTag = true;
				isTag = true;
				state.text(text.substring(0, i+1));
				text = text.substring(i+1);
				i = 0;
			} else
				i++;
		}
		if (text.length() != 0)
			state.text(text);
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
