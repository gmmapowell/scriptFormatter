package com.gmmapowell.script.processor;

import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;

// This should be unit tested
public class ProcessingUtils {
	public static void addSpans(ElementFactory factory, SpanBlock block, String text) {
		text = addRecursiveSpans(factory, block, null, text, false);
		if (text != null && text.length() > 0) {
			Span span = factory.span(null, text);
			block.addSpan(span);
		}
	}
	
	private static String addRecursiveSpans(ElementFactory factory, SpanBlock block, List<String> defaultStyle, String text, boolean inside) {
		int i=0;
		while (text != null && i<text.length()) {
			char c = text.charAt(i);
			if (c == '_' || c == '$' || c == '*') {
				if (i == 0 || text.charAt(i-1) == ' ') {
					if (i > 0) {
						addSpan(factory, block, defaultStyle, text.substring(0, i));
					}
					text = addRecursiveSpans(factory, block, styleOf(defaultStyle, c), text.substring(i+1), true); 
					i=0;
					continue;
				} else if (inside && (i == text.length()-1 || text.charAt(i+1) == ' ')) {
					addSpan(factory, block, defaultStyle, text.substring(0, i));
					return text.substring(i+1);
				}
			}
			i++;
		}
		return text;
	}

	private static void addSpan(ElementFactory factory, SpanBlock block, List<String> defaultStyle, String toAdd) {
		Span span;
		if (defaultStyle == null || defaultStyle.isEmpty())
			span = factory.span(null, toAdd);
		else if (defaultStyle.size() == 1)
			span = factory.span(defaultStyle.get(0), toAdd);
		else
			span = factory.lspan(defaultStyle, toAdd);
		block.addSpan(span);
	}

	private static List<String> styleOf(List<String> defaultStyle, char c) {
		String sty;
		switch (c) {
		case '_':
			sty = "italic";
			break;
		case '*':
			sty = "bold";
			break;
		case '$':
			sty = "tt";
			break;
		default:
			throw new RuntimeException("Cannot handle " + c);
		}
		List<String> ret = new ArrayList<>();
		if (defaultStyle != null)
			ret.addAll(defaultStyle);
		ret.add(sty);
		return ret;
	}
}
