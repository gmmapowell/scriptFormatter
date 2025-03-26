package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

public class CursorLocation {
	private Section section;
	
	CursorIndex curr = new CursorIndex();
	boolean atEnd;
	boolean endPara;

	Para para;
	List<List<SpanItem>> cxts = new ArrayList<>();
	
	public CursorLocation(Section section) {
		this.section = section;
		this.resetTo(new CursorIndex());
		findNextToken();
	}

	public void resetTo(CursorIndex to) {
		this.curr.setTo(to);
		this.moveToToken();
	}
	
	public void moveToToken() {
		if (curr.paraNum >= section.paras.size()) {
			atEnd = true;
			return;
		}
		this.para = section.paras.get(curr.paraNum);
		this.cxts.clear();
		List<SpanItem> sl = mapToSIs(this.para.spans);
		this.cxts.add(sl);
		for (int i=1;i<curr.spanIdxs.size();i++) {
			int k = curr.spanIdxs.get(i);
			NestedSpan ns = (NestedSpan) sl.get(k);
			HorizSpan hs = ns.nested;
			this.cxts.add(hs.items);
		}
	}

	private List<SpanItem> mapToSIs(List<HorizSpan> spans) {
		List<SpanItem> ret = new ArrayList<>();
		for (HorizSpan s : spans) {
			ret.add(new NestedSpan(s));
		}
		return ret;
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
			cxts.add(span.nested.items);
		}
		if (!atRealToken())
			advance();
	}

	public boolean atRealToken() {
		if (para.spans.isEmpty())
			return false;
		if (curr.top() >= top().size())
			return false;
		if (currentToken() instanceof NestedSpan)
			return false;
		return true;
	}
	
	public void advance() {
		if (atEnd)
			return;
		endPara = false;
		if (para.spans.isEmpty()) {
			curr.paraNum++;
			if (curr.paraNum >= section.paras.size()) {
				atEnd = true;
				return;
			} else if (curr.paraNum > 0) {
				endPara = true;
				return;
			}
		}
		while (!cxts.isEmpty()) {
			int k = curr.incr();
			if (k >= top().size()) {
				endPara = curr.pop();
				this.cxts.remove(this.cxts.size()-1);
				if (endPara)
					return;
			} else if (currentToken() instanceof NestedSpan) {
				while (true) {
					NestedSpan ns = (NestedSpan) currentToken();
					curr.spanIdxs.add(0);
					cxts.add(ns.nested.items);
					if (ns.nested.items.isEmpty()) {
						// we need to advance to the next token, so go back to the outer loop
						break;
					} else if (currentToken() instanceof NestedSpan) {
						continue; // push the next one
					} else {
						return;
					}
				}
				throw new NotImplementedException();
			} else {
				return;
			}
		}
		curr.paraNum++;
		if (curr.paraNum >= section.paras.size())
			atEnd = true;
		else
			endPara = true;
	}

	public boolean atEnd() {
		return this.atEnd;
	}

	public Para currentPara() {
		return para;
	}
	
	public List<SpanItem> top() {
		return cxts.get(cxts.size()-1);
	}

	public SpanItem currentToken() {
		if (curr.top() >= top().size())
			return null;
		return top().get(curr.top());
	}
	
	public CursorIndex index() {
		return curr;
	}
	
	public List<SpanItem> spine() {
		List<SpanItem> ret = new ArrayList<>();
		
		for (int i=0;i<curr.spanIdxs.size();i++) {
			SpanItem si = cxts.get(i).get(curr.spanIdxs.get(i));
			ret.add(si);
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return curr.toString();
	}
}
