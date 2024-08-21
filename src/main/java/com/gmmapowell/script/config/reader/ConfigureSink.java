package com.gmmapowell.script.config.reader;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.sink.blogger.BloggerSinkConfigListener;
import com.gmmapowell.script.modules.sink.epub.EPubSinkConfigListener;
import com.gmmapowell.script.modules.sink.html.HTMLSinkConfigListener;
import com.gmmapowell.script.modules.sink.pdf.PDFSinkConfigListener;
import com.gmmapowell.script.modules.sink.presenter.PresenterSinkConfigListener;
import com.gmmapowell.script.utils.FileWithLocation;
import com.gmmapowell.script.utils.LineArgsParser;

public class ConfigureSink implements ConfigListenerProvider {
	private final Map<String, Class<? extends ConfigListener>> sinks = new TreeMap<>();
	private final ReadConfigState state;

	public ConfigureSink(ReadConfigState state) {
		this.state = state;
		this.sinks.put("blogger", BloggerSinkConfigListener.class);
		this.sinks.put("epub", EPubSinkConfigListener.class);
		this.sinks.put("html", HTMLSinkConfigListener.class);
		this.sinks.put("pdf", PDFSinkConfigListener.class);
		this.sinks.put("presenter", PresenterSinkConfigListener.class);
	}

	@Override
	public ConfigListener make(LineArgsParser lap) {
		try {
			String type = lap.readArg();
			if (!this.sinks.containsKey(type)) {
				throw new CantHappenException("there is no sink '" + type + "'");
			}
			Class<? extends ConfigListener> clz = this.sinks.get(type);
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
