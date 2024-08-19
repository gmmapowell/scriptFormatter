package com.gmmapowell.script.config.reader;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.processors.blog.BlogProcessorConfigListener;
import com.gmmapowell.script.utils.FileWithLocation;
import com.gmmapowell.script.utils.LineArgsParser;

public class ConfigureProcessor implements ConfigListenerProvider {
	private final Map<String, Class<? extends ConfigListener>> processors = new TreeMap<>();
	private final ReadConfigState state;

	public ConfigureProcessor(ReadConfigState state) {
		this.state = state;
		this.processors.put("blog", BlogProcessorConfigListener.class);
	}

	@Override
	public ConfigListener make(LineArgsParser lap) {
		try {
			String type = lap.readArg();
			if (!this.processors.containsKey(type)) {
				throw new CantHappenException("there is no processor '" + type + "'");
			}
			Class<? extends ConfigListener> clz = this.processors.get(type);
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
