package com.gmmapowell.script.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class VarMap {
	private Map<String, VarValue> entries = new TreeMap<>();
	private int mynesting = 0;
	private VarValue curr = null;

	public void put(int nesting, String key, String value) {
		if (nesting <= 0)
			throw new RuntimeException("cannot have nesting " + nesting);
		if (nesting < mynesting)
			throw new RuntimeException("inconsistent nesting " + nesting);
		if (mynesting > 0 && nesting > mynesting) {
			curr.put(nesting, key, value);
			return;
		}
		this.mynesting = nesting;
		if (entries.containsKey(key)) {
			VarValue val = entries.get(key);
			val.add(value);
		} else {
			VarValue vv = new VarValue(value);
			entries.put(key, vv);
			this.curr = vv;
		}
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public Set<String> keySet() {
		return entries.keySet();
	}

	public List<VarValue> values(String s) {
		if (!entries.containsKey(s))
			return null;
		
		VarValue v = entries.get(s);
		return v.values();
	}

	public String remove(String s) {
		if (!entries.containsKey(s))
			return null;
		
		VarValue v = entries.remove(s);
		return v.unique();
	}

	public boolean containsKey(String s) {
		return entries.containsKey(s);
	}

	public Iterable<String> all(String k) {
		if (!entries.containsKey(k)) {
			return new ArrayList<>();
		}
		return entries.get(k).all();
	}
	
	public void delete(String k) {
		entries.remove(k);
	}

	public VarValue value(String k) {
		return entries.get(k);
	}
}
