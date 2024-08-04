package com.gmmapowell.script.config;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;

public class VarValue {
	private String str;
	private VarMap nested;
	private List<VarValue> list;
	
	public VarValue(String s) {
		this(s, null);
	}
	
	private VarValue(String s, VarMap n) {
		this.str = s;
		this.nested = n;
	}
	
	public VarValue add(String s) {
		if (list == null) {
			list = new ArrayList<>();
			list.add(new VarValue(str, nested));
			str = null;
		}
		list.add(new VarValue(s));
		return this;
	}
	
	public String unique() {
		if (str == null)
			throw new CantHappenException("this is a list entry");
		return str;
	}
	
	public List<VarValue> values() {
		if (list == null)
			return List.of(this);
		return list;
	}

	public Iterable<String> all() {
		if (str != null)
			return List.of(str);
		else {
			List<String> ret = new ArrayList<>();
			for (VarValue x : list)
				ret.add(x.str);
			return ret;
		}
	}

	public VarMap map() {
		return nested;
	}

	public void put(int nesting, String key, String value) {
		if (nested == null)
			nested = new VarMap();
		nested.put(nesting, key, value);
	}
}
