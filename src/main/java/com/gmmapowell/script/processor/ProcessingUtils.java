package com.gmmapowell.script.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.prose.CurrentState;

// This should be unit tested
public class ProcessingUtils {
	public static void addSpans(ElementFactory factory, CurrentState state, SpanBlock block, String text) {
		text = addRecursiveSpans(factory, state, block, null, text, ' ');
		if (text != null && text.length() > 0) {
			Span span = factory.span(null, text);
			block.addSpan(span);
		}
	}
	
	private static String addRecursiveSpans(ElementFactory factory, CurrentState state, SpanBlock block, List<String> defaultStyle, String text, char inside) {
		int i=0;
		while (text != null && i<text.length()) {
			char c = text.charAt(i);
			if (c == '_' || c == '$' || c == '*') {
				if (i < text.length()-1 && c == text.charAt(i+1)) {
					addSpan(factory, block, defaultStyle, text.substring(0, i+1));
					text = text.substring(i+2);
					i=0;
					continue;
				}
				if (inside == c /* && (inside == '$' || i == text.length()-1 || text.charAt(i+1) == ' ') */) { 
					// There is a problem with using these characters: that they may be used inside words
					// But at the same time it is very common to want to add a suffix to $..$s, so I've allowed that case.
					// Really, I think we should insist that people use __ if that's what they mean in words.
					addSpan(factory, block, defaultStyle, text.substring(0, i));
					return text.substring(i+1);
				} else {
					if (i > 0) {
						addSpan(factory, block, defaultStyle, text.substring(0, i));
					}
					text = addRecursiveSpans(factory, state, block, styleOf(defaultStyle, c), text.substring(i+1), c); 
					i=0;
					continue;
				} 
			} else if (c == '&') {
				if (i == text.length()-1) {
					// it's an & at the end of the line ... should just leave it there
				} else if (text.charAt(i+1) == '&') {
					// it's a && leave one of them
					addSpan(factory, block, defaultStyle, text.substring(0, i));
					text = text.substring(i+2);
					i=0;
					continue;
				} else {
					addSpan(factory, block, defaultStyle, text.substring(0, i));
					int j=i+1;
					while (j < text.length() && Character.isLetterOrDigit(text.charAt(j)))
						j++;
					String embed = text.substring(i+1, j);
					switch (embed) {
					case "sp":
						addSpan(factory, block, defaultStyle, " ");
						break;
					case "footnote":
						addSpan(factory, block, Arrays.asList("footnote-number"), Integer.toString(state.nextFootnoteMarker()));
						break;
					default:
						System.out.println("handle embedded " + embed);
						break;
					}
					if (j < text.length() && Character.isWhitespace(text.charAt(j)))
						text = text.substring(j+1); // skip the space character as well - it is technically just an EOC marker
					else
						text = text.substring(j);
					i = 0;
					continue;
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
