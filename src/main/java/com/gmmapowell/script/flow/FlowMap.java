package com.gmmapowell.script.flow;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;

public class FlowMap implements Iterable<Flow> {
	private final Map<String, Flow> flows = new TreeMap<>();
	private final Map<String, Object> oobs = new TreeMap<>();

	public void reset() {
		flows.clear();
		oobs.clear();
	}
	
	public void flow(String name) {
		if (flows.get(name) != null)
			throw new CantHappenException("cannot have duplicate flow with name " + name);
		flows.put(name, new Flow(name, true));
	}

	public void callbackFlow(String name) {
		if (flows.get(name) != null)
			throw new CantHappenException("cannot have duplicate callback flow with name " + name);
		flows.put(name, new Flow(name, false));
	}

	public <T> void bindOOB(String called, T oob) {
		oobs.put(called, oob);
	}

	@SuppressWarnings("unchecked")
	public <T> T oob(String called) {
		return (T) oobs.get(called);
	}
	
	public Flow get(String flow) {
		Flow ret = flows.get(flow);
		if (ret == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		return ret;
	}

	@Override
	public Iterator<Flow> iterator() {
		return flows.values().iterator();
	}
}
