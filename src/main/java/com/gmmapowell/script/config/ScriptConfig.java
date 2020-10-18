package com.gmmapowell.script.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.UtilException;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.DriveLoader;
import com.gmmapowell.script.loader.drive.Index;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.blogger.BloggerSink;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.sink.presenter.PresenterSink;
import com.gmmapowell.script.styles.StyleCatalog;

public class ScriptConfig implements Config {
	private final File root;
	private Loader loader;
	private List<Sink> sinks = new ArrayList<>();
	private Processor processor;
	private ElementFactory elf = new BlockishElementFactory();
	private Sink sink;
	private WebEdit webedit;
	private File index;

	private File workdir;
	public ScriptConfig(File root) {
		this.root = root;
	}
	
	public void handleLoader(Map<String, String> vars, String loader, File index, File workdir, boolean debug) throws ConfigException {
		if ("google-drive".equals(loader)) {
			String creds = vars.remove("credentials");
			if (creds == null)
				throw new ConfigException("credentials was not defined");
			String folder = vars.remove("folder");
			if (folder == null)
				throw new ConfigException("folder was not defined");
			this.loader = new DriveLoader(root, creds, folder, index, workdir, debug);
		} else
			throw new ConfigException("Unrecognized loader type " + loader);
	}

	public void handleOutput(Map<String, String> vars, String output, boolean debug, String sshid) throws ConfigException, Exception {
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
			String styles = vars.remove("styles");
			if (styles == null)
				throw new ConfigException("style catalog was not defined");
			StyleCatalog catalog = (StyleCatalog) Class.forName(styles).getConstructor().newInstance();
			sinks.add(new PDFSink(root, catalog, file, wantOpen, upload, debug, sshid));
			break;
		}
		case "blogger": {
			String creds = vars.remove("credentials");
			if (creds == null)
				throw new ConfigException("credentials was not defined");
			String blogUrl = vars.remove("blogurl");
			if (blogUrl == null)
				throw new ConfigException("blogurl was not defined");
			String posts = vars.remove("posts");
			if (posts == null)
				throw new ConfigException("posts was not defined");
			File pf = new File(posts);
			if (!pf.isAbsolute())
				pf = new File(root, posts);
			try {
				sinks.add(new BloggerSink(root, new File(creds), blogUrl, pf));
			} catch (Exception ex) {
				throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
			}
			break;
		}
		case "presenter": {
			String file = vars.remove("file");
			if (file == null)
				throw new ConfigException("output file was not defined");
			String show = vars.remove("show");
			boolean wantShow = false;
			if ("true".equals(show))
				wantShow = true;
			String upload = vars.remove("upload");
			sinks.add(new PresenterSink(root, file, wantShow, upload, debug));
			break;
		}
		default:
			throw new ConfigException("Unrecognized output type " + output);
		}
	}

	public void handleWebedit(Map<String, String> vars, String option, boolean debug, String sshid) throws ConfigException {
		String file = vars.remove("file");
		if (file == null)
			throw new ConfigException("webedit output file was not defined");
		String upload = vars.remove("upload");
		if (upload == null)
			throw new ConfigException("upload dir was not defined");
		String title = vars.remove("title");
		if (title == null)
			throw new ConfigException("title was not defined");
		this.webedit = new WebEdit(new File(root, file), upload, sshid, title);
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
			if (sinks.size() == 1)
				this.sink = sinks.get(0);
			else
				this.sink = new MultiSink(sinks);
			processor = ctor.newInstance(root, elf, sink, vars, debug);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConfigException)
				throw (ConfigException)e.getCause();
			Throwable t = UtilException.unwrap(e);
			t.printStackTrace(System.out);
			throw new ConfigException("could not create " + proc);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
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
		processor.process(files);
	}

	@Override
	public void show() {
		if (sink != null)
			sink.showFinal();
	}

	public void upload() throws Exception {
		if (sink != null)
			sink.upload();
		if (webedit != null) {
			webedit.upload();
		}
	}

	public void setIndex(File index) {
		this.index = index;
	}

	public void setWorkdir(File workdir) {
		this.workdir = workdir;
	}
}
