package com.gmmapowell.script.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.DriveLoader;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.simple.SimpleStyleCatalog;

public class ScriptConfig implements Config {
	private final File root;
	private Loader loader;
	private Sink sink;
	private Processor processor;
	private ElementFactory elf = new BlockishElementFactory();

	public ScriptConfig(File root) {
		this.root = root;
	}
	
	public void handleLoader(Map<String, String> vars, boolean debug) throws ConfigException {
		String creds = vars.remove("credentials");
		if (creds == null)
			throw new ConfigException("credentials was not defined");
		String folder = vars.remove("folder");
		if (folder == null)
			throw new ConfigException("folder was not defined");
		String index = vars.remove("index");
		if (index == null)
			throw new ConfigException("index was not defined");
		String downloads = vars.remove("downloads");
		if (downloads == null)
			downloads = "downloads";
		loader = new DriveLoader(root, creds, folder, index, downloads, debug);
	}

	public void handleOutput(Map<String, String> vars, boolean debug) throws ConfigException {
		String output = vars.remove("output");
		if (output == null)
			throw new ConfigException("output was not defined");
		String open = vars.remove("open");
		boolean wantOpen = false;
		if ("true".equals(open))
			wantOpen = true;
		StyleCatalog catalog = new SimpleStyleCatalog();
		sink = new PDFSink(root, catalog, output, wantOpen, debug);
	}

	@SuppressWarnings("unchecked")
	public void handleProcessor(Map<String, String> vars, boolean debug) throws ConfigException {
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
			ctor = clz.getConstructor(File.class, ElementFactory.class, Sink.class, Map.class, boolean.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ConfigException(proc + " does not have a suitable constructor");
		}
		try {
			processor = ctor.newInstance(root, elf, sink, vars, debug);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConfigException)
				throw (ConfigException)e.getCause();
			throw new ConfigException("could not create " + proc);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new ConfigException("could not create " + proc);
		}
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException {
		return loader.updateIndex();
	}

	@Override
	public void generate(FilesToProcess files) {
		processor.process(files);
	}

	@Override
	public void show() {
		sink.showFinal();
	}

}
