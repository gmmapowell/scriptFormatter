package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.zinutils.system.RunProcess;

import com.gmmapowell.script.processor.TextState;

public class IncludeCommand implements InlineCommand {
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

	private final TextState state;
	private final File file;
	private final Formatter formatter;
	private final List<Region> elides = new ArrayList<>();
	private Region select;
	private Indents indents;
	private Pattern stopAt;
	private boolean elideAtEnd;

	public IncludeCommand(TextState state, File file, Formatter formatter) {
		this.state = state;
		this.file = file;
		this.formatter = formatter;
	}

	@Override
	public void execute() throws IOException {
		try (LineNumberReader lnr = openSource()) {
			String line = null;
			int exdent = 0;
			int selectionIndent = 0;
			boolean primed = false;
			if (select != null) {
				exdent = select.exdent;
				while ((line = lnr.readLine()) != null) {
					if (select.pattern.matcher(line).find()) {
						primed = true;
						selectionIndent = indent(line, lnr.getLineNumber());
						break;
					}
//					System.out.println("skipping line " + line + " before select");
				}
				if (!primed)
					throw new RuntimeException("Never found selection beginning at " + select.from);
			}
			int curr = -1;
			boolean haveSkipped = false;
			Iterator<Region> ei = elides.iterator();
			Region lookFor = nextElide(ei);
			while (primed || (line = lnr.readLine()) != null) {
				int il = indent(line, lnr.getLineNumber());
				if (stopAt != null && stopAt.matcher(line).find()) {
					if (!haveSkipped && elideAtEnd) {
						elideThis(il);
					}
					break;
				}
				if (select != null && !primed && !formatter.isBlockIndent(selectionIndent, il)) {
//					System.out.println("selection over at " + line);
					break;
				}
				primed = false;
				if (curr != -1) {
//					System.out.println("comparing " + il + " to " + curr + " for " + line);
					if (formatter.isBlockIndent(curr, il)) {
						if (!haveSkipped) {
							elideThis(il);
							haveSkipped = true;
						}
						continue;  // skip this line
					}
					if (!haveSkipped)
						throw new RuntimeException("nothing was elided at " + file + ":" + lnr.getLineNumber());
					curr = -1;
				}
				if (lookFor != null && lookFor.pattern.matcher(line).find()) {
					curr = il;
					String what = lookFor.what;
//					System.out.println("Found " + lookFor.from + " removing " + what + " with curr = " + curr);
					lookFor = nextElide(ei);
					if (what.equals("inner"))
						curr++;
					else {
						if (!haveSkipped) {
							elideThis(il);
							haveSkipped = true;
						}
						continue; // skip this line
					}
				}
				if (indents == null || (il >= indents.min && il <= indents.max)) {
//					System.out.println("sinking " + line);
					formatter.format(line, exdent);
					haveSkipped = false;
				} else if (!haveSkipped) {
					elideThis(il);
					haveSkipped = true;
				}
			}
			if (lookFor != null)
				throw new RuntimeException("While processing " + file + ", did not come across " + lookFor.from);
		}
	}

	private LineNumberReader openSource() throws IOException {
		Reader is;
		if (state instanceof GitState && ((GitState)state).gittag() != null) {
			GitState gs = (GitState) state;
			// read it using gitshow
			RunProcess gitcmd = new RunProcess("git");
			gitcmd.arg("show");
			gitcmd.arg(gs.gittag() + ":" + file);
			gitcmd.captureStdout();
			gitcmd.redirectStderr(System.err);
			gitcmd.executeInDir(gs.gitdir());
			gitcmd.execute();
			is = new StringReader(gitcmd.getStdout());
		} else
			is = new FileReader(file, Charset.forName("UTF-8"));
		return new LineNumberReader(is);
	}

	private void elideThis(int il) throws IOException {
		state.newPara(state.formatAs());
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
					throw new RuntimeException("invalid mixing of tabs and spaces in indent processing " + file + ": " + lineNo);
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
	
	public void selectOnly(String from, String what, String exdent) {
		if (select != null)
			throw new RuntimeException("only one select is allowed");
		if (!elides.isEmpty())
			throw new RuntimeException("select must come before any &remove items");
		int exd = 0;
		if (exdent != null)
			exd = Integer.parseInt(exdent);
		select = new Region(from, what, exd);
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

}
