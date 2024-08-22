package com.gmmapowell.script.processor.configured;

import java.util.HashMap;
import java.util.Map;

import org.zinutils.reflection.Reflection;

public class ConfiguredState {
	private Map<Class<?>, Object> configs = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T require(Class<T> clz) {
		if (configs.containsKey(clz))
			return (T) configs.get(clz);
		T obj = (T) Reflection.create(clz);
		configs.put(clz, obj);
		return obj;
	}

}
