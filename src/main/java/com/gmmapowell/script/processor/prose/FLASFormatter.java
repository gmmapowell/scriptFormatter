package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.processor.TextState;

// To do this job properly, you should pull in the FLAS TDA parser as we do in the plugin ...
public class FLASFormatter implements Formatter {
	private final TextState state;

	public FLASFormatter(TextState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent) {
		text = text.replace("\t", "    ");
		state.newPara(state.formatAs());
		state.newSpan();
		int i = 0;
		
		// process indent
		while (i < text.length() && Character.isWhitespace(text.charAt(i)))
			i++;
		state.text(text.substring(0, i));
		text = text.substring(i);
		
		i = 0;
		boolean first = true;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (c == ' ') {
				String tx = text.substring(0, i);
				if (isKW(first, tx)) {
					state.nestSpan("bold");
					state.text(tx);
					state.popSpan();
				} else
					state.text(tx);
				state.text(" ");
				text = text.substring(i+1);
				i = 0;
			} else
				i++;
		}
		if (text.length() != 0) {
			if (isKW(first, text)) {
				state.nestSpan("bold");
				state.text(text);
				state.popSpan();
			} else
				state.text(text);
		}
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline <= 0 || thisline > firstline;
	}

	private boolean isKW(boolean first, String tx) {
		if (first) {
			switch (tx) {
			case "application":
			case "card":
			case "event":
			case "main":
			case "method":
			case "state":
			case "struct":
			case "template":
			case "title":
				return true;
			case "Number":
			case "String":
			case "Date":
				return true;
			}
		}
		switch (tx) {
		case "<-":
			return true;
		case "|":
			return true;
		case "=":
			return true;
		}
		return false;
	}
}
