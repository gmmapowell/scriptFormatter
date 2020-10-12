package com.gmmapowell.script.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.prose.CurrentState;
import com.gmmapowell.script.processor.prose.DocState;

// This should be unit tested
public class ProcessingUtils {
	public static void addSpans(ElementFactory factory, CurrentState state, SpanBlock block, String text) {
		text = addRecursiveSpans(factory, state, block, state.defaultSpans, text, ' ');
		if (text != null && text.length() > 0) {
			addSpan(factory, block, state.defaultSpans, text);
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
					case "sup": {
						int close = text.indexOf("&" + embed, j);
						if (close == -1)
							throw new RuntimeException("&" + embed + " without close");
						List<String> inner = new ArrayList<>(defaultStyle);
						inner.add(embed);
						String r = addRecursiveSpans(factory, state, block, inner, text.substring(j+1, close), ' ');
						if (r != null && r.length() > 0)
							addSpan(factory, block, inner, r);
						j = close + embed.length()+1;
						break;
					}
					default:
						throw new RuntimeException("handle embedded " + embed);
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

	public static void process(DocState st, String tx) {
		st.newSpan();
		processPart(st, tx, 0, tx.length());
		st.endSpan();
	}

	private static void processPart(DocState st, String tx, int i, int to) {
		int from = i;
		for (;i<tx.length();i++) {
			int q;
			if ((q = findRange(tx, i, to)) >= 0) {
				st.text(tx.substring(from, i));
				makeSpan(st, tx.charAt(i));
				processPart(st, tx, i+1, q-1);
				st.popSpan();
				from = i = q;
			} else if (q == -2) { // a double character; throw it away
				st.text(tx.substring(from, i));
				from = i+1;
			} else if ((q = getCommand(tx, i, to)) >= 0) {
				st.text(tx.substring(from, i));
				processCommand(st, tx.substring(i+1, q));
				from = i = q-1;
			} else if (q == -2) { // a double &; throw it away
				st.text(tx.substring(from, i));
				from = i+1;
			}
		}
	}

	private static int findRange(String tx, int i, int to) {
		// last on the line can't be doubled
		if (i+1 >= to)
			return -1;
		// is it a special char?
		char c = tx.charAt(i);
		if (c != '_' && c != '*' && c != '$')
			return -1;
		// it's a double ... return the magic value "-2"
		if (tx.charAt(i+1) == c)
			return -2;
		// find the matching one
		while (++i < to) {
			if (tx.charAt(i) == c)
				return i;
		}
		return -1;
	}

	private static void makeSpan(DocState st, char c) {
		switch (c) {
		case '_':
			st.nestSpan("italic");
			return;
		case '*':
			st.nestSpan("bold");
			return;
		case '$':
			st.nestSpan("tt");
			return;
		default:
			throw new NotImplementedException("figure out what to do with " + c);
		}
	}

	private static int getCommand(String tx, int i, int to) {
		// not a command
		if (tx.charAt(i) != '&')
			return -1;
		// last on the line can't be a command
		if (i+1 >= to)
			return -1;
		// it's a double ... return the magic value "-2"
		if (tx.charAt(i+1) != '&')
			return -2;

		while (i < to && Character.isLetterOrDigit(tx.charAt(i)))
			i++;

		return i+1;
	}

	private static void processCommand(DocState st, String substring) {
		// handle commands that started with &
	}
}
