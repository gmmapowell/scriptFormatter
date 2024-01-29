package com.gmmapowell.script.processor;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.SyncAfterFlow;

// This should be unit tested
public class ProcessingUtils {
	public static void process(TextState st, String tx) {
		st.newSpan();
		try {
			processPart(st, tx, 0, tx.length());
		} finally {
			st.endSpan();
		}
	}

	public static void processPart(TextState st, String tx, int i, int to) {
		int from = i;
		for (;i<to;i++) {
			int q;
			if ((q = findRange(tx, i, to)) >= 0) {
				if (i > from)
					st.text(tx.substring(from, i));
				makeSpan(st, tx.charAt(i));
				try {
					processPart(st, tx, i+1, q);
				} finally {
					st.popSpan();
				}
				i = q;
				from = i+1;
			} else if (q == -2) { // a double character; throw it away
				st.text(tx.substring(from, i+1));
				from = ++i + 1;
			} else if ((q = getCommand(st, tx, i, to)) >= 0) {
				if (i > from)
					st.text(tx.substring(from, i));
				processCommand(st, tx.substring(i+1, q));
				i = q;
				from = i+1;
			} else if (q == -2) { // a double &; throw it away
				st.text(tx.substring(from, i+1));
				from = ++i + 1;
			} else if (Character.isWhitespace(tx.charAt(i))) {
				if (i > from)
					st.text(tx.substring(from, i));
				st.op(new BreakingSpace());
				from = i+1;
			}
		}
		if (to > from)
			st.text(tx.substring(from, to));
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

	private static void makeSpan(TextState st, char c) {
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

	private static int getCommand(TextState state, String tx, int i, int to) {
		// not a command
		if (tx.charAt(i) != '&')
			return -1;
		// last on the line can't be a command
		if (++i >= to)
			return -1;
		// it's a double ... return the magic value "-2"
		if (tx.charAt(i) == '&')
			return -2;
		if (!Character.isLetterOrDigit(tx.charAt(i)))
			throw new ParsingException("cannot have just &: use && for a single & at " + state.inputLocation());

		while (i < to && Character.isLetterOrDigit(tx.charAt(i)))
			i++;

		return i;
	}

	private static void processCommand(TextState st, String cmd) {
		// handle commands that started with &
		switch (cmd) {
		case "footnote": {
			st.nestSpan("footnote-number");
			st.text(Integer.toString(st.nextFootnoteMarker()));
			st.popSpan();
			st.op(new SyncAfterFlow("footnotes"));
			break;
		}
		case "sp": {
			st.op(new BreakingSpace());
			break;
		}
		default: {
			throw new NoSuchCommandException(cmd, st.inputLocation());
		}
		}
	}
}
