package com.gmmapowell.script.config;

import java.util.HashMap;
import java.util.Map;

import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.modules.processors.doc.GlobalState;

public class SolidGlobalState implements GlobalState {
	private final Universe universe;
	private final ExtensionPointRepo eprepo;
	private final boolean debug;
	private final FlowMap flows;
	private final Map<Class<?>, Object> configs = new HashMap<>();

	public SolidGlobalState(Universe u, ExtensionPointRepo eprepo, boolean debug, FlowMap flows) {
		this.universe = u;
		this.eprepo = eprepo;
		this.debug = debug;
		this.flows = flows;
	}

	@Override
	public Universe getUniverse() {
		return universe;
	}
	
	@Override
	public ExtensionPointRepo extensions() {
		return eprepo;
	}

	@Override
	public FlowMap flows() {
		return flows;
	}

	@Override
	public Flow flow(String flow) {
		return flows.get(flow);
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
