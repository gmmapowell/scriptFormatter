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
		HorizSpan hs = loc.currentSpan();
		styles.addAll(hs.formats);
		SpanItem it = loc.currentToken();
//		int kk = this.item.get(0).get();
//		System.out.println("  hssz = " + hs.items.size() + "; kk = " + kk);
//		if (kk >= hs.items.size()) {
//			return new StyledToken(flow, para, span, item, styles, new ParaBreak());
//		}
//		SpanItem it = hs.items.get(kk);
		return new StyledToken(flow, loc.index(), styles, it);
	}

	/*
	public StyledToken dead() {
		boolean startAgain = false;
		top:
		while (true) {
			List<String> styles = new ArrayList<>();
			if (this.para >= si.paras.size())
				return null;
			System.out.println("  para = " + para);
			Para p = si.paras.get(para);
			styles.addAll(p.formats);
			if (span >= p.spans.size()) {
				this.para++;
				if (span == 0) {
					if (p.formats.contains("break"))
						startAgain = true;
					continue;
				}
				this.span = 0;
				System.out.println("returning parabreak");
				return new StyledToken(flow, para, span, item, styles, new ParaBreak());
			}
			System.out.println("  span = " + span);
			HorizSpan hs = p.spans.get(span);
			styles.addAll(hs.formats);
			if (startAgain) {
				styles.add("break");
				startAgain = false;
			}
			int kk = this.item.get(0).get();
			System.out.println("  hssz = " + hs.items.size() + "; kk = " + kk);
			if (kk >= hs.items.size()) {
				this.span++;
				reset();
				continue;
			}
			SpanItem it = hs.items.get(kk);
			int k = 0;
			while (it instanceof NestedSpan) {
				NestedSpan ns = (NestedSpan)it;
				k++;
				System.out.println("    nested " + k + " ns = " + ns.nested.items);
				if (this.item.size() <= k)
					this.item.add(new AtomicInteger(0));
				styles.addAll(ns.nested.formats);
				List<SpanItem> il = ns.nested.items;
				System.out.println("      il.size = " + il.size());
				AtomicInteger ki = item.get(k);
				System.out.println("      ki = " + ki.get());
				if (ki.get() >= il.size()) {
					item.get(k-1).incrementAndGet();
					while (item.size() > k)
						item.remove(k);
					System.out.println("      continuing");
					continue top;
				}
				it = il.get(ki.getAndIncrement());
			}
			if (k == 0) {
				item.get(0).incrementAndGet();
			}
			System.out.println("returning " + it);
			return new StyledToken(flow, para, span, item, styles, it);
		}
	}
	*/
	
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
