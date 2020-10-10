package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;

public class HTMLFormatter implements Formatter {
	private boolean inTag = false;

	@Override
	public Block format(ElementFactory ef, String text, int exdent) {
		text = text.replace("\t", "    ");
		if (exdent > 0)
			text = text.substring(exdent);
		SpanBlock ret = ef.block("preformatted");
		int i = 0;
		boolean isTag = false;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (inTag) {
				if (c == '>') {
					ret.addSpan(ef.span(isTag?"bold":null, text.substring(0, i)));
					text = text.substring(i);
					inTag = false;
					i = 1;
				} else if (c == ' ') {
					ret.addSpan(ef.span(isTag?"bold":null, text.substring(0, i)));
					isTag = false;
					text = text.substring(i);
					i = 1;
				} else if (c == '=') { // it's actually a more complicated condition than this, because we can have '=' in the attribute strings
					ret.addSpan(ef.span("bold", text.substring(0, i)));
					text = text.substring(i);
					i = 1;
				} else
					i++;
			} else if (c == '<') {
				inTag = true;
				isTag = true;
				ret.addSpan(ef.span(null, text.substring(0, i+1)));
				text = text.substring(i+1);
				i = 0;
			} else
				i++;
		}
		if (text.length() != 0)
			ret.addSpan(ef.span(null, text));
		return ret;
	}

	@Override
	public boolean isBlockIndent(int firstline, int thisline) {
		return thisline == -1 || thisline >= firstline;
	}
}
