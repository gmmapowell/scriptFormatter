package com.gmmapowell.script.modules.git;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

import org.zinutils.exceptions.WrappedException;
import org.zinutils.system.ProcessOutReader;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class GitShowProcessor extends Thread implements ProcessOutReader {
	private final ConfiguredState state;
	private final String branch;
	private final Pattern filespec;
	private final Pattern from;
	private final Pattern to;
	private final int numLines;
	private LineNumberReader input;
	private boolean doEcho;

	public GitShowProcessor(ConfiguredState sink, String branch, String filespec, String from, String to) {
		this.state = sink;
		this.branch = branch;
		this.filespec = Pattern.compile(filespec);
		this.from = from == null ? null : Pattern.compile(from);
		if (to == null) {
			this.to = null;
			this.numLines = Integer.MAX_VALUE;
		} else if (to.startsWith("+")) {
			numLines = Integer.parseInt(to);
			this.to = null;
		} else {
			this.to = Pattern.compile(to);
			this.numLines = Integer.MAX_VALUE;
		}
	}
	
	@Override
	public void echoStream(boolean doEcho) {
		this.doEcho = doEcho;
	}

	@Override
	public void read(InputStream inputStream) {
		input = new LineNumberReader(new InputStreamReader(inputStream));		
	}

	@Override
	public void run() {
		try {
			boolean matchedFile = false, matchedRange = false;
			boolean copying = false, inrange = false;
			int cntLines = Integer.MAX_VALUE;
			String s;
			while ((s = input.readLine()) != null) {
				if (doEcho) {
					System.out.println((copying? " || " : " -- ") + s);
				}
				if (s.startsWith("+++")) {
					copying = filespec.matcher(s).find();
					matchedFile |= copying;
					inrange = from == null;
					matchedRange |= inrange;
					continue;
				}
				if (s.startsWith("diff --git ")) {
					copying = false;
					continue;
				}
				if (s.startsWith("\\ No newline at end of file")) {
					continue;
				}
				if (copying) {
					if (s.startsWith("-") || (s.startsWith("@@") && s.endsWith("@@")) || (s.startsWith("@@ ") && s.contains(" @@ ")))
						continue;
					if (!inrange) {
						if (from.matcher(s).find()) {
							inrange = true;
							cntLines = numLines;
							matchedRange = true;
						}
					}
					if (inrange) {
						StringBuilder sb = new StringBuilder(s.substring(1));
						int i=0;
						while (i<sb.length()) {
							if (sb.charAt(i) == '\t') {
								sb.delete(i, i+1); // delete the tab
								sb.insert(i, "  "); // insert two spaces
								i+=2;
							} else if (sb.length() >= i+4 && sb.subSequence(i, i+4).equals("    ")) {
								sb.delete(i, i+2); // delete 2 of the four
								i+=2;
							} else
								break;
						}
						s = sb.toString();
						state.newPara("blockquote");
						state.newSpan();
						state.text(s);
						if (cntLines-- == 0) // postdecrement because we are "including" the first line for free
							inrange = false;
						if (to != null && to.matcher(s).find())
							inrange = false;
					}
				}
			}
			if (!matchedFile) {
				System.err.println("Did not find file like " + filespec + " in " + branch);
			} else if (!matchedRange) {
				System.err.println("Did not find starting expression " + from + " in " + filespec + " in " + branch);
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}
}
