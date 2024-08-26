package com.gmmapowell.script.flow;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;

public class FlowMap implements Iterable<Flow> {
	private final Map<String, Flow> flows = new TreeMap<>();

	public void flow(String name) {
		flows.put(name, new Flow(name, true));
	}

	public void callbackFlow(String name) {
		flows.put(name, new Flow(name, false));
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
