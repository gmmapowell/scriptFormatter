package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class CursorLocation {
	private Section si;
	
	CursorIndex curr = new CursorIndex();
	private boolean atEnd = false;
	private boolean needBreak;
	boolean endPara;

	Para para;
	List<HorizSpan> spans = new ArrayList<>();
	
	public CursorLocation(Section si) {
		this.si = si;
		this.resetTo(new CursorIndex());
		findNextToken();
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
			endPara = true;
			curr.paraNum++;
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

	private void findNextToken() {
		if (atEnd || endPara)
			return;
		if (atRealToken()) {
			return;
		}
		while (currentToken() instanceof NestedSpan) {
			NestedSpan span = (NestedSpan) currentToken();
			curr.spanIdxs.add(0);
			spans.add(span.nested);
		}
		if (!atRealToken())
			advance();
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
		endPara = false;
		if (needBreak) {
			needBreak = false;
			moveToToken();
			if (atEnd || needBreak || atRealToken())
				return;
		}
		if (para.spans.isEmpty()) {
			curr.paraNum++;
			if (curr.paraNum >= si.paras.size()) {
				atEnd = true;
				return;
			} else if (curr.paraNum > 0) {
				endPara = true;
				return;
			}
		}
		while (true) {
			int k = curr.incr();
			if (k >= currentSpan().items.size()) {
				needBreak = curr.pop();
				this.spans.remove(this.spans.size()-1);
				if (needBreak)
					return;
			} else {
				moveToToken();
				return;
			}
		}
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
		if (curr.top() >= currentSpan().items.size())
			return null;
		return currentSpan().items.get(curr.top());
	}
	
	public CursorIndex index() {
		return curr;
	}
	
	@Override
	public String toString() {
		return curr.toString();
	}
}
