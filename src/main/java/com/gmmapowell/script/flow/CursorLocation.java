package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class CursorLocation {
	private Section si;
	
	CursorIndex curr = new CursorIndex();
	private boolean atEnd = false;
	private boolean needBreak;

	Para para;
	List<HorizSpan> spans = new ArrayList<>();
	
	public CursorLocation(Section si) {
		this.si = si;
		this.resetTo(new CursorIndex());
		if (!atEnd && !needBreak && !atRealToken()) {
			advance();
		}
	}

	public void resetTo(CursorIndex to) {
		this.curr.setTo(to);
		this.moveToToken();
	}
	
	private HorizSpan getSpan(List<HorizSpan> spans, int idx) {
		if (idx >= spans.size())
			return null;
		return spans.get(idx);
	}
	
	public void moveToToken() {
		if (curr.paraNum >= si.paras.size()) {
			atEnd = true;
			return;
		}
		this.para = si.paras.get(curr.paraNum);
		HorizSpan s = getSpan(this.para.spans, this.curr.spanIdxs.get(0));
		if (s == null) {
			if (!this.para.spans.isEmpty())
				needBreak = true;
			return;
		}
		this.spans.clear();
		this.spans.add(s);
		for (int i=1;i<curr.spanIdxs.size();i++) {
			int k = curr.spanIdxs.get(i);
			NestedSpan ns = (NestedSpan) s.items.get(k);
			s = ns.nested;
			this.spans.add(s);
		}
	}
	
	public boolean atRealToken() {
		if (para.spans.isEmpty())
			return false;
		if (curr.top() >= currentSpan().items.size())
			return false;
		if (currentToken() instanceof NestedSpan)
			return false;
		return true;
	}
	
	public void advance() {
		if (atEnd)
			return;
		if (needBreak) {
			needBreak = false;
			moveToToken();
			if (atEnd || needBreak || atRealToken())
				return;
		}
		while (para.spans.isEmpty()) {
			curr.paraNum++;
			if (curr.paraNum >= si.paras.size()) {
				atEnd = true;
				return;
			}
		}
		int k = curr.incr();
		if (k >= currentSpan().items.size()) {
			needBreak = curr.pop();
			return;
		}
		/*
		if (needBreak) {
			needBreak = false;
			curr.paraNum++;
			curr.spanNum = 0;
			curr.itemNums.clear();
			moveToToken();
			return;
		}
		if (items.get(0) instanceof NestedSpan) {
			items.add(items.get(0));
			curr.itemNums.add(-1);
			advance();
			return;
		}
		while (curr.itemNums.size() > 1) {
			int k = curr.itemNums.get(curr.itemNums.size()-1);
			k++;
			if (k >= ((NestedSpan)items).nested.items.size()) {
				items.remove(items.size()-1);
				curr.itemNums.remove(curr.itemNums.size()-1);
			} else {
				curr.itemNums.set(curr.itemNums.size()-1, k);
			}
		}
		int k = curr.itemNums.get(0);
		k++;
		if (k >= span.items.size()) {
			curr.spanNum++;
			curr.itemNums.clear();
			moveToToken();
			return;
		}
		this.items.add(span.items.get(k));
		curr.itemNums.set(0, k);
		*/
	}

	public boolean atEnd() {
		return this.atEnd;
	}

	public boolean needBreak() {
		return this.needBreak;
	}

	public Para currentPara() {
		return para;
	}

	public HorizSpan currentSpan() {
		return this.spans.get(this.spans.size()-1);
	}
	
	public SpanItem currentToken() {
		HorizSpan me = currentSpan();
		return me.items.get(curr.top());
	}
	
	public CursorIndex index() {
		return curr;
	}
	
	@Override
	public String toString() {
		return curr.toString();
	}
}
