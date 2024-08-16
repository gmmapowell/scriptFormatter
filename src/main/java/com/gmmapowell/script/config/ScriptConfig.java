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
import com.gmmapowell.geofs.gdw.GoogleDriveWorld;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.DriveLoader;
import com.gmmapowell.script.loader.drive.Index;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.blogger.BloggerSink;
import com.gmmapowell.script.sink.capture.CaptureSinkInFile;
import com.gmmapowell.script.sink.epub.EPubSink;
import com.gmmapowell.script.sink.html.HTMLSink;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.sink.presenter.PresenterSink;
import com.gmmapowell.script.styles.ConfigurableStyleCatalog;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Utils;

public class ScriptConfig implements Config {
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
	
	public void handleLoader(VarMap vars, String loader, Place index, Region workdir, boolean debug) throws ConfigException {
		this.debug = debug;
		if ("google-drive".equals(loader)) {
			String creds = vars.remove("credentials");
			if (creds == null)
				throw new ConfigException("credentials was not defined");
			String folder = vars.remove("folder");
			if (folder == null)
				throw new ConfigException("folder was not defined");
			
			try {
				// TODO: I feel that this should be elsewhere
				Place credsPath = root.placePath(creds);
				new GoogleDriveWorld(universe, "ScriptFormatter", credsPath);
			} catch (GeneralSecurityException | IOException ex) {
				throw new ConfigException(ex.toString());
			}
			// TODO: this should not depend on Google Drive 
			this.loader = new DriveLoader(universe, root, workdir, index, folder, debug);
		} else
			throw new ConfigException("Unrecognized loader type " + loader);
	}

	public void handleOutput(VarMap vars, String output, boolean debug, String sshid) throws ConfigException, Exception {
		switch (output) {
		case "epub": {
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
			styles = Utils.subenvs(styles);
			StyleCatalog catalog;
			try {
				catalog = (StyleCatalog) Class.forName(styles).getConstructor().newInstance();
			} catch (ClassNotFoundException ex) {
				try {
					catalog = new ConfigurableStyleCatalog(root.place(styles), debug);
				} catch (Exception e2) {
					throw new ConfigException(e2.getMessage());
				}
			}
			sinks.add(new EPubSink(root, catalog, file, wantOpen, upload, debug, sshid, vars));
			break;
		}
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
			styles = Utils.subenvs(styles);
			StyleCatalog catalog;
			try {
				catalog = (StyleCatalog) Class.forName(styles).getConstructor().newInstance();
			} catch (ClassNotFoundException ex) {
				try {
					catalog = new ConfigurableStyleCatalog(root.place(styles), debug);
				} catch (Exception e2) {
					throw new ConfigException(e2.getMessage());
				}
			}
			sinks.add(new PDFSink(root, catalog, file, wantOpen, upload, debug, sshid, vars));
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
			boolean localOnly = false;
			String lo = vars.remove("local");
			if (lo != null && "true".equalsIgnoreCase(lo))
				localOnly = true;
			Place saveContentAs = null;
			String sca = vars.remove("saveAs");
			if (sca != null)
				saveContentAs = root.place(sca);
			Place pf = root.placePath(posts);
			Place cp = root.placePath(creds);
			try {
				sinks.add(new BloggerSink(root, cp, blogUrl, pf, localOnly, saveContentAs));
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ConfigException("Error creating BloggerSink: " + ex.getMessage());
			}
			break;
		}
		case "html": {
			String storeInto = vars.remove("store");
			if (storeInto == null)
				throw new ConfigException("store directory was not defined");
//			String creds = vars.remove("credentials");
//			if (creds == null)
//				throw new ConfigException("credentials was not defined");
//			String blogUrl = vars.remove("blogurl");
//			if (blogUrl == null)
//				throw new ConfigException("blogurl was not defined");
//			String posts = vars.remove("posts");
//			if (posts == null)
//				throw new ConfigException("posts was not defined");
//			File pf = new File(posts);
//			if (!pf.isAbsolute())
//				pf = new File(root, posts);
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
}
