package com.gmmapowell.script.modules.doc.includecode;

import com.gmmapowell.script.processor.configured.ConfiguredState;

// To do this job properly, you should pull in the FLAS TDA parser as we do in the plugin ...
public class GoFormatter implements Formatter {
	private final ConfiguredState state;

	public GoFormatter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void format(String text, int exdent, boolean withHighlight) {
		text = text.replace("\t", "    ");
		state.newPara(withHighlight?"highlight":null);
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
			if (c == ' ' || c == '(') {
				String tx = text.substring(0, i);
				if (isKW(first, tx)) {
					state.nestSpan("bold");
					state.text(tx);
					state.popSpan();
				} else {
					state.text(tx);
					first = false;
				}
				state.text(new String(new char[] { c }));
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
		state.endPara();
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return true;
	}

	private boolean isKW(boolean first, String tx) {
		switch (tx) {
		case "for":
		case "func":
		case "go":
		case "if":
		case "import":
		case "interface":
		case "package":
		case "return":
		case "struct":
		case "type":
		case "var":
			return true;
		}

		if (tx.startsWith("panic"))
			return true;
		
		return false;
	}
}
