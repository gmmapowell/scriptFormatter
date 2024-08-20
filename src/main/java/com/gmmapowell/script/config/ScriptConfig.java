package com.gmmapowell.script.config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.UtilException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.Index;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.capture.CaptureSinkInFile;
import com.gmmapowell.script.sink.epub.EPubSink;
import com.gmmapowell.script.sink.html.HTMLSink;
import com.gmmapowell.script.sink.presenter.PresenterSink;
import com.gmmapowell.script.styles.ConfigurableStyleCatalog;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Utils;

public class ScriptConfig implements Config {
	// This is a hack to make regression tests quicker.
	// TODO: it should be configured from the environment
	private final boolean ALLOW_UPLOADS = false;
	
	private final Universe universe;
	private final Region root;
	private boolean debug;
	private Loader loader;
	private List<Sink> sinks = new ArrayList<>();
	private Processor processor;
	private ElementFactory elf = new BlockishElementFactory();
	private Sink sink;
	private WebEdit webedit;
	private Place index;
	private Region workdir;
	
	public ScriptConfig(Universe universe, Region root) {
		this.universe = universe;
		this.root = root;
	}
	
	public void handleOutput(VarMap vars, String output, boolean debug, String sshid) throws ConfigException, Exception {
		switch (output) {
		case "html": {
			String storeInto = vars.remove("store");
			if (storeInto == null)
				throw new ConfigException("store directory was not defined");
			try {
				sinks.add(new HTMLSink(root, storeInto));
			} catch (Exception ex) {
				throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
			}
			break;
		}
		case "presenter": {
			String file = vars.remove("file");
			if (file == null)
				throw new ConfigException("output file was not defined");
			String meta = vars.remove("meta");
			if (meta == null)
				throw new ConfigException("meta file was not defined");
			String show = vars.remove("show");
			boolean wantShow = false;
			if ("true".equals(show))
				wantShow = true;
			String upload = vars.remove("upload");
			sinks.add(new PresenterSink(root, file, meta, wantShow, upload, debug));
			break;
		}
		default:
			throw new ConfigException("Unrecognized output type " + output);
		}
	}

	public void handleWebedit(VarMap vars, String option, boolean debug, String sshid) throws ConfigException {
		String file = vars.remove("file");
		if (file == null)
			throw new ConfigException("webedit output file was not defined");
		String upload = vars.remove("upload");
		if (upload == null)
			throw new ConfigException("upload dir was not defined");
		String title = vars.remove("title");
		if (title == null)
			throw new ConfigException("title was not defined");
		if (Boolean.parseBoolean(option))
			this.webedit = new WebEdit(root.place(file), upload, sshid, title);
	}
	
	
	@SuppressWarnings("unchecked")
	public void handleProcessor(VarMap vars, String proc, boolean debug) throws ConfigException {
		Class<? extends Processor> clz;
		if (debug)
			System.out.println("handling processor " + proc);
		try {
			clz = (Class<? extends Processor>) Class.forName(proc);
		} catch (ClassNotFoundException ex) {
			throw new ConfigException("Could not create a processor: " + proc);
		}
		if (!Processor.class.isAssignableFrom(clz))
			throw new ConfigException(proc + " is not a Processor");
		if (debug)
			System.out.println("have processor class " + clz);
		Constructor<? extends Processor> ctor;
		try {
			ctor = clz.getConstructor(Region.class, ElementFactory.class, Sink.class, VarMap.class, boolean.class);
			if (debug)
				System.out.println("have processor ctor " + ctor);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ConfigException(proc + " does not have a suitable constructor");
		}
		try {
			if (sinks.size() == 1)
				this.sink = sinks.get(0);
			else
				this.sink = new MultiSink(sinks);
			// TODO: should be configured
			// TODO: we also want to change the responsibilities here, so this will be very different later
			this.sink = new CaptureSinkInFile(root, sink);
			processor = ctor.newInstance(root, elf, sink, vars, debug);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConfigException)
				throw (ConfigException)e.getCause();
			Throwable t = UtilException.unwrap(e);
			t.printStackTrace(System.out);
			throw new ConfigException("could not create " + proc);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | IOException e) {
			UtilException.unwrap(e).printStackTrace(System.out);
			throw new ConfigException("could not create " + proc);
		}
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException {
		if (loader == null) {
	        Index currentIndex = Index.read(index, workdir);
			return currentIndex;
		}
		if (webedit != null)
			loader.createWebeditIn(webedit.file, webedit.title);
		return loader.updateIndex();
	}

	@Override
	public void generate(FilesToProcess files) throws IOException {
		if (processor == null) {
			if (debug) {
				System.out.println("no processor has been defined");
			}
			return;
		}
		processor.process(files);
	}

	@Override
	public void show() {
		if (sink != null)
			sink.showFinal();
	}

	public void upload() throws Exception {
		if (ALLOW_UPLOADS) {
			if (sink != null)
				sink.upload();
			if (webedit != null) {
				webedit.upload();
			}
		}
	}

	public void setIndex(Place index) {
		this.index = index;
	}

	public void setWorkdir(Region workdir) {
		this.workdir = workdir;
	}

	public void loader(Loader loader) {
		this.loader = loader;
	}

	public void sink(Sink sink) {
		this.sinks.add(sink);
	}

	public Sink makeSink() throws IOException {
		Sink sink;
		if (sinks.size() == 1)
			sink = sinks.get(0);
		else
			sink = new MultiSink(sinks);
		// TODO: should be configured
		// TODO: we also want to change the responsibilities here, so this will be very different later
		sink = new CaptureSinkInFile(root, sink);
		return sink;
	}

	public void processor(Processor processor) {
		this.processor = processor;
	}
}
