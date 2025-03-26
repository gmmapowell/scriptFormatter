package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class Cursor implements Comparable<Cursor> {
	private final String flow;
	private final Section section;
	private final CursorLocation loc;

	public Cursor(String name, Section section) {
		this.loc = new CursorLocation(section);
		this.flow = name;
		this.section = section;
	}

	public String flowName() {
		return flow;
	}

	public String format() {
		return section.format;
	}

	public StyledToken next() {
//		System.out.println("next(), this.loc = " + this.loc);
		if (loc.endPara) {
			List<String> styles = new ArrayList<>();
			loc.endPara = false;
			Para p = loc.previousPara();
			styles.addAll(p.formats);
			styles.remove("break");
			return new StyledToken(flow, loc.index(), styles, new ParaBreak());
		}
		if (this.loc.currentPara() == null)
			return null;

		StyledToken ret = figureThisToken();
		loc.advance();
		return ret;
	}
	
	private StyledToken figureThisToken() {
		List<String> styles = new ArrayList<>();
		Para p = loc.currentPara();
		if (p == null)
			return null;
		styles.addAll(p.formats);
		styles.remove("break");
		if (loc.endPara) {
			loc.endPara = false;
			return new StyledToken(flow, loc.index(), styles, new ParaBreak());
		}
		for (SpanItem hs : loc.spine()) {
			if (hs instanceof NestedSpan) {
				NestedSpan ns = (NestedSpan) hs;
				styles.addAll(ns.nested.formats);
			}
		}
		SpanItem it = loc.currentToken();
		return new StyledToken(flow, loc.index(), styles, it);
	}
	
	public void backTo(StyledToken lastAccepted) {
		if (lastAccepted == null)
			resetTo(new CursorIndex());
		else
			lastAccepted.resetMe(this);
	}

	public void resetTo(CursorIndex to) {
		this.loc.resetTo(to);
	}

	public boolean isFlow(String enable) {
		return this.flow.equals(enable);
	}

	@Override
	public int compareTo(Cursor o) {
		// TODO: it is insane that we sort by name
		// When we create the flows, we should specify the order in which we wish to see
		// them processed, and then do that.
		return flow.compareTo(o.flow);
	}

	@Override
	public String toString() {
		return "Cursor[" + flow + "]";
	}
}
