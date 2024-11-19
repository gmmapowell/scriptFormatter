package com.gmmapowell.script.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.modules.processors.doc.GlobalState;

public class SolidGlobalState implements GlobalState {
	private final Universe universe;
	private final Region root;
	private final ExtensionPointRepo eprepo;
	private final boolean debug;
	private final FlowMap flows;
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final Set<Finisher> finishers;

	public SolidGlobalState(Universe u, Region root, ExtensionPointRepo eprepo, Set<Finisher> finishers, boolean debug, FlowMap flows) {
		this.universe = u;
		this.root = root;
		this.eprepo = eprepo;
		this.finishers = finishers;
		this.debug = debug;
		this.flows = flows;
	}

	@Override
	public Universe getUniverse() {
		return universe;
	}

	@Override
	public Region getRoot() {
		return root;
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
		T obj;
		try {
			obj = (T) Reflection.create(clz, this);
		} catch (Exception ex) {
			try {
				obj = (T) Reflection.create(clz);
			} catch (Exception e2) {
				throw new CantHappenException(clz + " does not have a constructor with a GlobalState arg or default constructor");
			}
		}			
		configs.put(clz, obj);
		return obj;
	}
	
	@Override
	public void addFinisher(Finisher f) {
		this.finishers.add(f);
	}

	@Override
	public boolean debug() {
		return debug;
	}
}
