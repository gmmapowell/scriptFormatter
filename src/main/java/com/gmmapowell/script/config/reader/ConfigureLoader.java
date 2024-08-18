package com.gmmapowell.script.config.reader;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.loaders.google.GoogleDriveConfigListener;
import com.gmmapowell.script.utils.FileWithLocation;
import com.gmmapowell.script.utils.LineArgsParser;

public class ConfigureLoader implements ConfigListenerProvider {
	private final Map<String, Class<? extends ConfigListener>> loaders = new TreeMap<>();
	private final ReadConfigState state;

	public ConfigureLoader(ReadConfigState state) {
		this.state = state;
		this.loaders.put("google-drive", GoogleDriveConfigListener.class);
	}

	@Override
	public ConfigListener make(LineArgsParser lap) {
		try {
			String type = lap.readArg();
			if (!this.loaders.containsKey(type)) {
				throw new CantHappenException("there is no loader '" + type + "'");
			}
			Class<? extends ConfigListener> clz = this.loaders.get(type);
			Constructor<?>[] ctors = clz.getConstructors();
			for (Constructor<?> c : ctors) {
				if (c.getParameterCount() == 1 && FileWithLocation.class.isAssignableFrom(c.getParameters()[0].getType()))
					return (ConfigListener) c.newInstance(state);
			}
			throw new CantHappenException("the class '" + clz + "' does not have a constructor that takes a state");
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
