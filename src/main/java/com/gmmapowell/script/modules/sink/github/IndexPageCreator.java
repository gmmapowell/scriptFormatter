package com.gmmapowell.script.modules.sink.github;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.LifecycleObserver;

public class IndexPageCreator implements DocumentOutline, Creator<IndexPageCreator, ScannerAtState>, LifecycleObserver {
	public class Title {
		private String sno;
		private String title;
		private Place storedIn;

		public Title(String sno, String title) {
			this.sno = sno;
			this.title = title;
		}
		
		@Override
		public String toString() {
			return "["+sno+"]:" + title + (storedIn != null ? "{" + storedIn + "}" : "");
		}
	}
	
	private class Match {
		private int outerfrom, outerto;
		private String var;
		
		public Match(int outerfrom, int outerto, String var) {
			super();
			this.outerfrom = outerfrom;
			this.outerto = outerto;
			this.var = var;
		}
	}

	private final static Pattern ifpatt = Pattern.compile("%IF\\{([^%\\{]*)\\}(.*)%ENDIF");
	private final static Pattern varpatt = Pattern.compile("%\\{([^%\\{]*)\\}");
	private final List<Title> titles = new ArrayList<>();
	private final List<Title> places = new ArrayList<>();
	private final PrintWriter idx;
	private Place idxpostp;
	
	public IndexPageCreator(Place plc, Place idxprep, Place idxpostp) {
		this.idxpostp = idxpostp;
		Writer w = plc.writer();
		idx = new PrintWriter(w);
		idxprep.writeTo(idx);
	}

	@Override
	public void entry(int level, String tocFormat, String title, String anchor) {
		System.out.println("entry " + title + " format = " + tocFormat);
		titles.add(new Title(tocFormat, title));
	}

	@Override
	public IndexPageCreator create(ScannerAtState quelle) {
		return this;
	}

	public void haveFile(Place place) {
		String name = place.name();
		Title title = titles.remove(0);
		title.storedIn = place;
		this.places.add(title);
		if (title.title != null) {
			idx.println("<li><a href='html/" + name + "'>" + (title.sno.length() > 0 ? title.sno + " " : "") + title.title + "</a>");
		}
	}

	public void processToken(StyledToken tok) {
//		System.out.println("ipg tok " + tok.it.getClass());
	}

	@Override
	public void allDone(GlobalState state) {
		idxpostp.writeTo(idx);
		idx.close();
		
		Title prev = null, curr= null;
		
		for (Title next : places) {
			if (curr != null) {
				updateVars(curr, prev, next);
			}
			prev = curr;
			curr = next;
		}
		if (curr != null) {
			updateVars(curr, prev, null);
		}
	}

	private void updateVars(Title curr, Title prev, Title next) {
		StringWriter sw = new StringWriter();
		curr.storedIn.lines(new LineListener() {
			@Override
			public void line(String line) {
				Matcher ifm = ifpatt.matcher(line);
				while (ifm.find()) {
					String var = ifm.group(1);
					String inner = ifm.group(2);
					if (haveVar(var)) {
						line = line.substring(0, ifm.start()) + inner + line.substring(ifm.end());
					} else {
						line = line.substring(0, ifm.start()) + line.substring(ifm.end());
					}
				}
				Matcher matcher = varpatt.matcher(line);
				List<Match> matches = new ArrayList<>();
				while (matcher.find()) {
					matches.add(0, new Match(matcher.start(0), matcher.end(0), line.substring(matcher.start(1), matcher.end(1))));
				}
				while (!matches.isEmpty()) {
					Match match = matches.remove(0);
					line = line.substring(0, match.outerfrom) + replaceVar(match.var) + line.substring(match.outerto);
				}
				sw.append(line);
			}

			private boolean haveVar(String var) {
				if (var.equalsIgnoreCase("prev")) {
					return prev != null;
				} else if (var.equalsIgnoreCase("next")) {
					return next != null;
				} else {
					return false;
				}
			}

			private String replaceVar(String var) {
				if (var.equalsIgnoreCase("title")) {
					return curr.title;
				} else if (var.equalsIgnoreCase("prev")) {
					if (prev == null) {
						return "";
					}
					return prev.storedIn.name();
				} else if (var.equalsIgnoreCase("next")) {
					if (next == null) {
						return "";
					}
					return next.storedIn.name();					
				} else {
					System.err.println("unrecognized var: " + var);
					return "${" + var + "}";
				}
			}
		});
		curr.storedIn.store(sw.toString());
	}
}
