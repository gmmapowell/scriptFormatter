package com.gmmapowell.script.config.reader;

import java.lang.reflect.Constructor;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;
import org.zinutils.utils.StringUtil;

import com.gmmapowell.script.utils.FileWithLocation;
import com.gmmapowell.script.utils.LineArgsParser;

public class ModuleFinder implements ConfigListenerProvider {
	private final ReadConfigState state;

	public ModuleFinder(ReadConfigState state) {
		this.state = state;
	}

	@Override
	public ConfigListener make(LineArgsParser lap) {
		String type = lap.readArg();
		String clzname;
		if (lap.hasMore()) {
			// the more is an explicit module loader
			clzname = lap.readArg();
		} else {
			// the built-in "processor" modules
			String name = "Install" + StringUtil.capitalize(type) + "Module";
			clzname = "com.gmmapowell.script.modules." + name;
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends ConfigListener> clz = (Class<? extends ConfigListener>) Class.forName(clzname);
			Constructor<?>[] ctors = clz.getConstructors();
			for (Constructor<?> c : ctors) {
				if (c.getParameterCount() == 1 && FileWithLocation.class.isAssignableFrom(c.getParameters()[0].getType()))
					return (ConfigListener) c.newInstance(state);
			}
			throw new CantHappenException("the class '" + clz + "' does not have a constructor that takes a state");
		} catch (ClassNotFoundException ex) {
			throw new CantHappenException("there is no loader for '" + type + "': cannot find class " + clzname);
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
