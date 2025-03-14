package com.gmmapowell.script.config.reader;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.doc.flasgrammar.FlasGrammarConfigListener;
import com.gmmapowell.script.modules.doc.includecode.IncludeCodeConfigListener;
import com.gmmapowell.script.modules.doc.placedate.PlaceDateConfigListener;
import com.gmmapowell.script.modules.doc.scanmode.ScanmodeConfigListener;
import com.gmmapowell.script.modules.doc.toc.TOCConfigListener;
import com.gmmapowell.script.modules.git.GitConfigListener;
import com.gmmapowell.script.utils.FileWithLocation;

// TODO: I think this should actually be created early on and be available in state
public class NestedModuleCreator {
	private final Map<String, Class<? extends ConfigListener>> modules = new TreeMap<>();
	private final ReadConfigState state;

	public NestedModuleCreator(ReadConfigState state) {
		this.state = state;
		this.modules.put("flas-grammar", FlasGrammarConfigListener.class);
		this.modules.put("include-code", IncludeCodeConfigListener.class);
		this.modules.put("placedate", PlaceDateConfigListener.class);
		this.modules.put("scanmode", ScanmodeConfigListener.class);
		this.modules.put("toc", TOCConfigListener.class);
		
		this.modules.put("git", GitConfigListener.class);
	}

	public void register(String name, Class<? extends ConfigListener> clz) {
		this.modules.put(name, clz);
	}
	
	public ModuleConfigListener module(String mod) {
		try {
			if (!this.modules.containsKey(mod)) {
				throw new CantHappenException("there is no module '" + mod + "'");
			}
			Class<? extends ConfigListener> clz = this.modules.get(mod);
			Constructor<?>[] ctors = clz.getConstructors();
			for (Constructor<?> c : ctors) {
				if (c.getParameterCount() == 1 && FileWithLocation.class.isAssignableFrom(c.getParameters()[0].getType()))
					return (ModuleConfigListener) c.newInstance(state);
			}
			throw new CantHappenException("the class '" + clz + "' does not have a constructor that takes a state");
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
