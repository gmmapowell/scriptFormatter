package com.gmmapowell.script.config;

import java.util.HashMap;
import java.util.Map;

import org.zinutils.reflection.Reflection;

import com.gmmapowell.script.modules.processors.doc.GlobalState;

public class SolidGlobalState implements GlobalState {
	private final boolean debug;
	private final ExtensionPointRepo eprepo;
	private final Map<Class<?>, Object> configs = new HashMap<>();

	public SolidGlobalState(ExtensionPointRepo eprepo, boolean debug) {
		this.eprepo = eprepo;
		this.debug = debug;
	}

	@Override
	public ExtensionPointRepo extensions() {
		return eprepo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T requireState(Class<T> clz) {
		if (configs.containsKey(clz))
			return (T) configs.get(clz);
		T obj = (T) Reflection.create(clz);
		configs.put(clz, obj);
		return obj;
	}
	
	@Override
	public boolean debug() {
		return debug;
	}
}
