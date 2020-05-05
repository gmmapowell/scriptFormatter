package com.gmmapowell.script.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.DriveLoader;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.blogger.BloggerSink;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.simple.SimpleStyleCatalog;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class ScriptConfig implements Config {
	private final File root;
	private Loader loader;
	private List<Sink> sinks = new ArrayList<>();
	private Processor processor;
	private ElementFactory elf = new BlockishElementFactory();
	private Sink sink;

	public ScriptConfig(File root) {
		this.root = root;
	}
	
	public void handleLoader(Map<String, String> vars, String loader, String index, String workdir, boolean debug) throws ConfigException {
		if ("google-drive".equals(loader)) {
			String creds = vars.remove("credentials");
			if (creds == null)
				throw new ConfigException("credentials was not defined");
			String folder = vars.remove("folder");
			if (folder == null)
				throw new ConfigException("folder was not defined");
			if (workdir == null)
				workdir = "downloads";
			this.loader = new DriveLoader(root, creds, folder, index, workdir, debug);
		} else
			throw new ConfigException("Unrecognized loader type " + loader);
	}

	public void handleOutput(Map<String, String> vars, String output, boolean debug) throws ConfigException {
		switch (output) {
		case "pdf": {
			String file = vars.remove("file");
			if (file == null)
				throw new ConfigException("output file was not defined");
			String open = vars.remove("open");
			boolean wantOpen = false;
			if ("true".equals(open))
				wantOpen = true;
			String upload = vars.remove("upload");
			StyleCatalog catalog = new SimpleStyleCatalog();
			sinks.add(new PDFSink(root, catalog, file, wantOpen, upload, debug));
			break;
		}
		case "blogger": {
			String creds = vars.remove("credentials");
			if (creds == null)
				throw new ConfigException("credentials was not defined");
			try {
				sinks.add(new BloggerSink(root, new File(creds)));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}
		default:
			throw new ConfigException("Unrecognized output type " + output);
		}
	}

	@SuppressWarnings("unchecked")
	public void handleProcessor(Map<String, String> vars, String proc, boolean debug) throws ConfigException {
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
			this.sink = new MultiSink(sinks);
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
		if (sink != null)
			sink.showFinal();
	}

	public void upload() throws JSchException, SftpException {
		if (sink != null)
			sink.upload();
	}
}
