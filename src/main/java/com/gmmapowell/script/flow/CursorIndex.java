package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class CursorIndex {
	int paraNum = 0;
	int spanNum = 0;
	List<Integer> itemNums = new ArrayList<>();

	public void setTo(CursorIndex idx) {
		this.paraNum = idx.paraNum;
		this.spanNum = idx.spanNum;
		this.itemNums = new ArrayList<>();
		for (int x : idx.itemNums) {
			this.itemNums.add(x);
		}
	}
	
	@Override
	public String toString() {
		return itemNums + "." + spanNum + "." + paraNum;
	}

}
