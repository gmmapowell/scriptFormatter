package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;

// To do this job properly, you should pull in the FLAS TDA parser as we do in the plugin ...
public class FLASFormatter implements Formatter {

	@Override
	public Block format(ElementFactory ef, String text, int exdent) {
		text = text.replace("\t", "    ");
		SpanBlock ret = ef.block("preformatted");
		int i = 0;
		
		// process indent
		while (i < text.length() && Character.isWhitespace(text.charAt(i)))
			i++;
		ret.addSpan(ef.span(null, text.substring(0, i)));
		text = text.substring(i);
		
		i = 0;
		boolean first = true;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (c == ' ') {
				String tx = text.substring(0, i);
				ret.addSpan(ef.span(isKW(first, tx)?"bold":null, tx));
				ret.addSpan(ef.span(null, " "));
				text = text.substring(i+1);
				i = 0;
			} else
				i++;
		}
		if (text.length() != 0)
			ret.addSpan(ef.span(isKW(first, text)?"bold":null, text));
		return ret;
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
			case "template":
			case "title":
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
