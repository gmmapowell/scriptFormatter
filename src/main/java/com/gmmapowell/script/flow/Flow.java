package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class Flow {
	public final String name;
	private final boolean main;
	public final List<Section> sections = new ArrayList<>();

	public Flow(String name, boolean main) {
		this.name = name;
		this.main = main;
	}
	
	public boolean isMain() {
		return main;
	}
	
	@Override
	public String toString() {
		return "Flow[" + name + (main?"*":"") + "]";
	}

	public Flow renamedTo(String newName) {
		Flow ret = new Flow(newName, main);
		ret.sections.addAll(this.sections);
		return ret;
	}
}
