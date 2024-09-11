package com.gmmapowell.script.config.reader;

import java.util.Map;
import java.util.TreeMap;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.utils.SBLocation;

public class ReadConfigState extends SBLocation {
	public ScriptConfig config;
	private NestedModuleCreator modules;
	private final Map<String, Class<? extends ConfigListener>> processors = new TreeMap<>();
	public boolean debug = false;
	public Place index = null;
	public Region workdir = null;
	public String sshid = null;
	public String what = null, type = null;
	public int wline = 0;
	public Region root;
	
	public ReadConfigState(Region root, ScriptConfig sc) {
		this.root = root;
		this.config = sc;
		this.modules = new NestedModuleCreator(this);
	}

	public void registerModule(String name, Class<? extends ConfigListener> clz) {
		modules.register(name, clz);
	}

	public void registerProcessor(String name, Class<? extends ConfigListener> clz) {
		processors.put(name, clz);
	}
	
	public boolean hasProcessor(String type) {
		return processors.containsKey(type);
	}
	
	public Class<? extends ConfigListener> processor(String type) {
		return processors.get(type);
	}

	public Universe universe() {
		return root.getUniverse();
	}

	public ModuleConfigListener module(String mod) {
		return modules.module(mod);
	}
}
