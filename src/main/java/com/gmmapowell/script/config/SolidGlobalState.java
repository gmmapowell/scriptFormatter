package com.gmmapowell.script.config;

import java.util.HashMap;
import java.util.Map;

import org.zinutils.reflection.Reflection;

import com.gmmapowell.script.modules.processors.doc.GlobalState;

public class SolidGlobalState implements GlobalState {
	private final ExtensionPointRepo eprepo;
	private final Map<Class<?>, Object> configs = new HashMap<>();

	public SolidGlobalState(ExtensionPointRepo eprepo) {
		this.eprepo = eprepo;
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
}
