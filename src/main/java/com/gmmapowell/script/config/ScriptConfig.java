package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.loader.Index;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.capture.CaptureSinkInFile;

public class ScriptConfig implements Config, ExtensionPointRepo {
	// This is a hack to make regression tests quicker.
	// TODO: it should be configured from the environment
	private final boolean ALLOW_UPLOADS = false;
	
	private final Region root;
	private boolean debug;
	private Loader loader;
	private List<Sink> sinks = new ArrayList<>();
	private Processor processor;
	private WebEdit webedit;
	private Place index;
	private Region workdir;
	
	private final ListMap<Class<?>, Class<?>> extensionPointClasses = new ListMap<>();
	
	public ScriptConfig(Universe universe, Region root) {
		this.root = root;
	}
	
	@Override
	public void prepare() throws Exception {
//		loader.prepare();
//		processor.prepare();
		for (Sink s : sinks)
			s.prepare();
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
			throw new CantHappenException("no processor has been defined");
		}
		processor.process(files);
	}

	@Override
	public void show() {
		for (Sink s : sinks)
			s.showFinal();
	}

	public void upload() throws Exception {
		if (ALLOW_UPLOADS) {
			for (Sink s : sinks)
				s.upload();
			if (webedit != null) {
				webedit.upload();
			}
		}
	}

	public void finish() throws Exception {
		for (Sink s : sinks)
			s.finish();
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

	public void webedit(WebEdit webEdit) {
		this.webedit = webEdit;
	}

	public void check() throws ConfigException {
		if (processor == null)
			throw new ConfigException("no processor was specified");
	}

	@Override
	public <T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg) {
		Map<String, T> ret = new TreeMap<>();
		if (extensionPointClasses.contains(clz)) {
			for (Class<?> m : extensionPointClasses.get(clz)) {
				@SuppressWarnings("unchecked")
				T nep = (T) Reflection.create(m, ctorArg);
				if (ret.containsKey(nep.name())) {
					throw new CantHappenException("duplicate extension point for " + nep.name());
				}
				ret.put(nep.name(), nep);
			}
		}
		return ret;
	}

	public <T, Z extends T> void bindExtensionPoint(Class<T> ep, Class<Z> impl) {
		extensionPointClasses.add(ep, impl);
	}
}
