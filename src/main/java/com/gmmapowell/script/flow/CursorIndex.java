package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class CursorIndex {
	int paraNum = 0;
	List<Integer> spanIdxs = new ArrayList<>();

	public CursorIndex() {
		this.spanIdxs.add(0);
	}
	
	public void setTo(CursorIndex idx) {
		this.paraNum = idx.paraNum;
		this.spanIdxs = new ArrayList<>();
		for (int x : idx.spanIdxs) {
			this.spanIdxs.add(x);
		}
	}
	
	public int top() {
		return spanIdxs.get(spanIdxs.size()-1);
	}

	public int incr() {
		int k = spanIdxs.get(spanIdxs.size()-1);
		k++;
		spanIdxs.set(spanIdxs.size()-1, k);
		return k;
	}

	public void pop() {
		spanIdxs.remove(spanIdxs.size()-1);
	}
	
	@Override
	public String toString() {
		return paraNum + "" + spanIdxs;
	}

}
