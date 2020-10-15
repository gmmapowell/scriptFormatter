package com.gmmapowell.script.sink.pdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;

public class Cursor implements Comparable<Cursor> {
	private final String flow;
	private final Section si;
	private int para;
	private int span;
	private List<AtomicInteger> item;

	public Cursor(String name, Section si) {
		this.flow = name;
		this.si = si;
		this.para = 0;
		this.span = 0;
		reset();
	}

	private void reset() {
		this.item = new ArrayList<AtomicInteger>();
		this.item.add(new AtomicInteger(0));
	}

	public StyledToken next() {
//		System.out.println("next()");
		top:
		while (true) {
			List<String> styles = new ArrayList<>();
			if (this.para >= si.paras.size())
				return null;
//			System.out.println("  para = " + para);
			Para p = si.paras.get(para);
			styles.addAll(p.formats);
			if (span >= p.spans.size()) {
				this.para++;
				this.span = 0;
				return new StyledToken(flow, para, span, item, styles, new ParaBreak());
			}
//			System.out.println("  span = " + span);
			HorizSpan hs = p.spans.get(span);
			styles.addAll(hs.formats);
			int kk = this.item.get(0).get();
//			System.out.println("  hssz = " + hs.items.size() + "; kk = " + kk);
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
//				System.out.println("    nested " + k);
				if (this.item.size() <= k)
					this.item.add(new AtomicInteger(0));
				styles.addAll(ns.nested.formats);
				List<SpanItem> il = ns.nested.items;
//				System.out.println("      il.size = " + il.size());
				AtomicInteger ki = item.get(k);
				if (ki.get() >= il.size()) {
					item.get(k-1).incrementAndGet();
					while (item.size() > k)
						item.remove(k);
//					System.out.println("      continuing");
					continue top;
				}
				it = il.get(ki.getAndIncrement());
			}
			if (k == 0) {
				item.get(0).incrementAndGet();
			}
			return new StyledToken(flow, para, span, item, styles, it);
		}
	}

	public void backTo(StyledToken lastAccepted) {
		if (lastAccepted == null)
			resetTo(0, 0, Arrays.asList(0));
		else
			lastAccepted.resetMe(this);
	}

	public void resetTo(int para, int span, List<Integer> item) {
		this.para = para;
		this.span = span;
		this.item = new ArrayList<>();
		for (int i=0;i<item.size();i++)
			this.item.add(new AtomicInteger(item.get(i)));
	}

	public boolean isFlow(String enable) {
		return this.flow.equals(enable);
	}

	@Override
	public int compareTo(Cursor o) {
		return flow.compareTo(o.flow);
	}

	@Override
	public String toString() {
		return "Cursor[" + flow + "]";
	}
}
