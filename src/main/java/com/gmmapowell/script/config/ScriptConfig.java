package com.gmmapowell.script.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Map;

import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.DriveLoader;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.PDFSink;

public class ScriptConfig implements Config {
	private final File root;
	private Loader loader;
	private Sink sink;
	private Processor processor;

	public ScriptConfig(File root) {
		this.root = root;
	}
	
	public void handleLoader(Map<String, String> vars) throws ConfigException {
		String creds = vars.remove("credentials");
		if (creds == null)
			throw new ConfigException("credentials was not defined");
		String index = vars.remove("index");
		if (index == null)
			throw new ConfigException("index was not defined");
		loader = new DriveLoader(creds, index);
	}

	public void handleOutput(Map<String, String> vars) throws ConfigException {
		String output = vars.remove("output");
		if (output == null)
			throw new ConfigException("output was not defined");
		sink = new PDFSink(root, output);
	}

	@SuppressWarnings("unchecked")
	public void handleProcessor(Map<String, String> vars) throws ConfigException {
		String proc = vars.remove("processor");
		if (proc == null)
			throw new ConfigException("processor was not defined");
		Class<? extends Processor> clz;
		try {
			clz = (Class<? extends Processor>) Class.forName(proc);
		} catch (ClassNotFoundException ex) {
			throw new ConfigException("Could not create a processor: " + proc);
		}
		if (!Processor.class.isAssignableFrom(clz))
			throw new ConfigException(proc + " is not a Processor");
		Constructor<? extends Processor> ctor;
		try {
			ctor = clz.getConstructor(Sink.class, Map.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ConfigException(proc + " does not have a suitable constructor");
		}
		try {
			processor = ctor.newInstance(sink, vars);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConfigException)
				throw (ConfigException)e.getCause();
			throw new ConfigException("could not create " + proc);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new ConfigException("could not create " + proc);
		}
	}

	@Override
	public void updateIndex() throws IOException, GeneralSecurityException {
		loader.updateIndex();
	}

	@Override
	public void generate() {
	}

	@Override
	public void show() {
		sink.showFinal();
	}

}
