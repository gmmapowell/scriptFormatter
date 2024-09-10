package com.gmmapowell.script.modules.doc.includecode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class DoInclusion {
	public class Indents {
		private final int min;
		private final int max;

		public Indents(int min, int max) {
			this.min = min;
			this.max = max;
		}
	}

	public class Region {
		private final String from;
		private final Pattern pattern;
		private final String what;
		private final int exdent;

		public Region(String from, String what, int exd) {
			this.from = from;
			this.pattern = Pattern.compile(from);
			this.what = what;
			this.exdent = exd;
		}
	}

	private final ConfiguredState state;
	private final Formatter formatter;
	private final Place place;
	private Region select;
	private final List<Region> elides = new ArrayList<>();
	private Indents indents;
	private Pattern stopAt;
	private boolean elideAtEnd;

	public DoInclusion(ConfiguredState state, Place place, Formatter formatter) {
		this.state = state;
		this.place = place;
		this.formatter = formatter;
	}

	public void selectOnly(String from, String what, String exdent) {
		if (select != null)
			throw new RuntimeException("only one select is allowed");
		if (!elides.isEmpty())
			throw new RuntimeException("select must come before any &remove items");
		int exd = 0;
		if (exdent != null)
			exd = Integer.parseInt(exdent);
		select = new Region(from, what, exd);
		primed = false;
	}

	public void butRemove(String from, String what) {
		elides.add(new Region(from, what, 0));
	}

	public void indents(int min, int max) {
		if (indents != null)
			throw new RuntimeException("indents may only be specified once");
		indents = new Indents(min, max);
	}

	public void stopAt(String end, String elide) {
		this.stopAt = Pattern.compile(end);
		if (elide != null)
			elideAtEnd = Boolean.parseBoolean(elide);
	}

	boolean primed = true, stopped = false;
	int exdent = 0;
	int selectionIndent = 0;
	private Region lookFor;
	private Iterator<Region> ei;
	int curr = -1;
	boolean haveSkipped = false;

	public void include() throws IOException {
		ei = elides.iterator();
		lookFor = nextElide(ei);
		state.pushFormat("preformatted");
		openSource((n,s) -> { try { processLine(n, s); } catch (IOException ex) { throw WrappedException.wrap(ex); }});
		state.popFormat("preformatted");
		if (lookFor != null)
			throw new RuntimeException("While processing " + place + ", did not come across " + lookFor.from);
		if (select != null && !primed)
			throw new RuntimeException("Never found selection beginning at " + select.from);
	}

	private void openSource(NumberedLineListener lsnr) throws IOException {
		place.lines(lsnr);
	}

	private void processLine(int lineNo, String line) throws IOException {
//		System.out.println("processing included line " + line + " stopped = " + stopped);
		System.out.print("include " + lineNo + ": " + primed + ": ");
		boolean allowStop = true;
		if (stopped) {
			System.out.println("stopped: " + line);
			return;
		} else if (!primed) {
			exdent = select.exdent;
			if (select.pattern.matcher(line).find()) {
				primed = true;
				allowStop = false;
				selectionIndent = indent(line, lineNo);
			} else {
				System.out.println("skipping line before select: " + line);
				return;
			}
		}
		int il = indent(line, lineNo);
		if (stopAt != null && stopAt.matcher(line).find()) {
			if (!haveSkipped && elideAtEnd) {
				elideThis(il);
			}
			stopped = true;
			return;
		}
		if (allowStop && select != null && !formatter.isBlockIndent(selectionIndent, il)) {
				System.out.println("selection over at: " + line);
			stopped = true;
			return;
		}
		if (curr != -1) {
				System.out.println("comparing " + il + " to " + curr + " for " + line);
			if (formatter.isBlockIndent(curr, il)) {
				if (!haveSkipped) {
					elideThis(il);
					haveSkipped = true;
				}
				return;  // skip this line
			}
			if (!haveSkipped)
				throw new RuntimeException("nothing was elided at " + place + ":" + lineNo);
			curr = -1;
		}
		if (lookFor != null && lookFor.pattern.matcher(line).find()) {
			curr = il;
			String what = lookFor.what;
				System.out.println("Found " + lookFor.from + " removing " + what + " with curr = " + curr);
			lookFor = nextElide(ei);
			if (what.equals("inner"))
				curr++;
			else {
				if (!haveSkipped) {
					elideThis(il);
					haveSkipped = true;
				}
				return; // skip this line
			}
		}
		if (indents == null || (il >= indents.min && il <= indents.max)) {
				System.out.println("formatting: " + line);
			formatter.format(line, exdent);
			haveSkipped = false;
		} else if (!haveSkipped) {
			elideThis(il);
			haveSkipped = true;
		}
	}

	private void elideThis(int il) throws IOException {
		state.newPara();
		state.newSpan();
		state.text(makeElide(il));
		state.endPara();
	}

	private String makeElide(int il) {
		StringBuilder ret = new StringBuilder();
		for (int i=0;i<il;i++)
			ret.append(" ");
		ret.append("...");
		return ret.toString();
	}

	private int indent(String line, int lineNo) {
		int ret = 0;
		int i=0;
		for (;i<line.length() && Character.isWhitespace(line.charAt(i));i++) {
			if (line.charAt(i) == '\t') {
				if (ret % 4 != 0)
					throw new RuntimeException("invalid mixing of tabs and spaces in indent processing " + place + ": " + lineNo);
				ret += 4;
			} else
				ret++;
		}
		if (i == line.length())
			return -1; // it is all white space
		return ret;
	}

	private Region nextElide(Iterator<Region> e) {
		if (!e.hasNext())
			return null;
		return e.next();
	}
}
