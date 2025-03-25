package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class CursorLocation {
	private Section si;
	
	CursorIndex curr = new CursorIndex();
	private boolean atEnd = false;
	private boolean needBreak;

	Para para;
	HorizSpan span;
	List<SpanItem> items = new ArrayList<>();
	
	
	public CursorLocation(Section si) {
		this.si = si;
		this.resetTo(new CursorIndex());
	}

	public void resetTo(CursorIndex to) {
		this.curr.setTo(to);
		this.moveToToken();
	}
	
	public void moveToToken() {
		while (true) {
			if (curr.paraNum >= si.paras.size()) {
				atEnd = true;
				return;
			}
			this.para = si.paras.get(curr.paraNum);
			if (curr.spanNum >= this.para.spans.size()) {
				if (curr.spanNum > 0) {
					needBreak = true;
					return;
				}
				curr.paraNum++;
				continue;
			}
			this.span = this.para.spans.get(curr.spanNum);
			if (this.span.items.isEmpty()) {
				curr.spanNum++;
				continue;
			}
			curr.itemNums.add(0);
			this.items.add(span.items.get(0));
			return;
		}
	}
	
	public void advance() {
		if (atEnd)
			return;
		if (needBreak) {
			needBreak = false;
			curr.paraNum++;
			curr.spanNum = 0;
			curr.itemNums.clear();
			moveToToken();
			return;
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
		return span;
	}
	
	public SpanItem currentToken() {
		return span.items.get(0);
	}
	
	public CursorIndex index() {
		return curr;
	}
	
	@Override
	public String toString() {
		return curr.toString();
	}
}
