package com.gmmapowell.script.processor.configured;

import java.util.HashMap;
import java.util.Map;

import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.utils.SBLocation;

public class ConfiguredState extends SBLocation {
	private Map<Class<?>, Object> configs = new HashMap<>();
	private ExtensionPointRepo eprepo;

	public ConfiguredState(ExtensionPointRepo eprepo, Place x) {
		this.eprepo = eprepo;
		super.processingFile(x.name());
	}

	@SuppressWarnings("unchecked")
	public <T> T require(Class<T> clz) {
		if (configs.containsKey(clz))
			return (T) configs.get(clz);
		T obj = (T) Reflection.create(clz);
		configs.put(clz, obj);
		return obj;
	}

	public ExtensionPointRepo extensions() {
		return eprepo;
	}
}
