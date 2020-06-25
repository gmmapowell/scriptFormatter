package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.sink.Sink;

public class IncludeCommand implements InlineCommand {
	public class Elide {
		private final String from;
		private final Pattern pattern;
		private final String what;

		public Elide(String from, String what) {
			this.from = from;
			this.pattern = Pattern.compile(from);
			this.what = what;
		}
	}

	private final File file;
	private final Formatter formatter;
	private final List<Elide> elides = new ArrayList<>();

	public IncludeCommand(File file, Formatter formatter) {
		this.file = file;
		this.formatter = formatter;
	}

	@Override
	public void execute(Sink sink, ElementFactory ef) throws IOException {
		Iterator<Elide> ei = elides.iterator();
		Elide lookFor = nextElide(ei);
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file, Charset.forName("UTF-8")))) {
			String line;
			int curr = -1;
			boolean haveSkipped = false;
			while ((line = lnr.readLine()) != null) {
				int il = indent(line, lnr.getLineNumber());
				if (curr != -1) {
					System.out.println("comparing " + il + " to " + curr + " for " + line);
					if (il >= curr) {
						if (!haveSkipped) {
							elideThis(sink, ef, il);
							haveSkipped = true;
						}
						continue;  // skip this line
					}
					if (!haveSkipped)
						throw new RuntimeException("nothing was elided at " + file + ":" + lnr.getLineNumber());
					curr = -1;
					haveSkipped = false;
				}
				if (lookFor != null && lookFor.pattern.matcher(line).find()) {
					curr = il;
					String what = lookFor.what;
					System.out.println("Found " + lookFor.from + " removing " + what + " with curr = " + curr);
					lookFor = nextElide(ei);
					if (what.equals("inner"))
						curr++;
					else {
						elideThis(sink, ef, il);
						haveSkipped = true;
						continue; // skip this line
					}
				}
				sink.block(formatter.format(ef, line));
			}
		}
		if (lookFor != null)
			throw new RuntimeException("While processing " + file + ", did not come across " + lookFor.from);
	}

	private void elideThis(Sink sink, ElementFactory ef, int il) throws IOException {
		SpanBlock eb = ef.block("preformatted");
		eb.addSpan(ef.span(null, makeElide(il)));
		sink.block(eb);
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
		for (int i=0;i<line.length() && Character.isWhitespace(line.charAt(i));i++) {
			if (line.charAt(i) == '\t') {
				if (ret % 4 != 0)
					throw new RuntimeException("invalid mixing of tabs and spaces in indent processing " + file + ": " + lineNo);
				ret += 4;
			} else
				ret++;
		}
		return ret;
	}

	private Elide nextElide(Iterator<Elide> e) {
		if (!e.hasNext())
			return null;
		return e.next();
	}

	public void butRemove(String from, String what) {
		elides.add(new Elide(from, what));
	}

}
