package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class Cursor implements Comparable<Cursor> {
	private final String flow;
	private final Section si;
	private final CursorLocation loc;

	public Cursor(String name, Section si) {
		this.loc = new CursorLocation(si);
		this.flow = name;
		this.si = si;
	}

	public String flowName() {
		return flow;
	}

	public String format() {
		return si.format;
	}

	public StyledToken next() {
		System.out.println("next(), this.loc = " + this.loc);
		if (this.loc.atEnd())
			return null;

		if (loc.endPara) {
			loc.advance();
			return next();
		}
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
		if (loc.needBreak()) {
			return new StyledToken(flow, loc.index(), styles, new ParaBreak());
		}
		for (HorizSpan hs : loc.spans) {
			styles.addAll(hs.formats);
		}
		SpanItem it = loc.currentToken();
//		int kk = this.item.get(0).get();
//		System.out.println("  hssz = " + hs.items.size() + "; kk = " + kk);
//		if (kk >= hs.items.size()) {
//			return new StyledToken(flow, para, span, item, styles, new ParaBreak());
//		}
//		SpanItem it = hs.items.get(kk);
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
