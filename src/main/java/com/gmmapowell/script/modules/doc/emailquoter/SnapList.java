package com.gmmapowell.script.modules.doc.emailquoter;

import java.util.ArrayList;
import java.util.List;

public class SnapList {
	final List<String> list = new ArrayList<>();
	
	public SnapList() {
	}
	
	public SnapList(List<String> list) {
		this.list.addAll(list);
	}
	
	public static SnapList parse(String s) {
		SnapList sl = new SnapList();
		String[] parts = s.split(" ");
		if (!"@snap".equals(parts[0]))
			throw new RuntimeException("not a valid snap list: " + s);
		if (parts.length < 2)
			throw new RuntimeException("not a valid snap list: " + s);

		for (int i=1;i<parts.length;i++) {
			sl.list.add(parts[i]);
		}
		return sl;
	}

	@Override
	public String toString() {
		return list.toString();
	}
}