package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.MultiSink;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.capture.CaptureSinkInFile;
import com.gmmapowell.script.sink.capture.FlowDumper;

public class ScriptConfig implements Config {
	private final String have_internet = System.getenv("HAVE_INTERNET");
	private final boolean CONFIGURE_WITH_INTERNET = have_internet == null || ("true".equals(have_internet));
	private final String enable_uploads = System.getenv("ENABLE_UPLOADS");
	private final boolean WANT_UPLOADS = enable_uploads == null || ("true".equals(enable_uploads));
	private final boolean ALLOW_UPLOADS = CONFIGURE_WITH_INTERNET && WANT_UPLOADS;
	
	private final Universe universe;
	private final Region root;
	private boolean debug;
	private Loader loader;
	private final List<Sink> sinks = new ArrayList<>();
	private final Set<Finisher> finishers = new HashSet<>();
	private Processor processor;
	private WebEdit webedit;
	private ExtensionPointRepo eprepo = new CreatorExtensionPointRepo(null);

	private final FlowMap flows = new FlowMap();

	public ScriptConfig(Universe universe, Region root) {
		this.universe = universe;
		this.root = root;
	}
	
	@Override
	public Region root() {
		return this.root;
	}

	public ExtensionPointRepo extensions() {
		return eprepo;
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
			throw new CantHappenException("you should have specified a loader");
		}
		if (webedit != null)
			loader.createWebeditIn(webedit.file, webedit.title);
		return loader.updateIndex();
	}

	
	@Override
	public void indexLoad(FilesToProcess files) {
		loader.reload(files);
	}

	@Override
	public void reset() {
		flows.reset();
	}
	
	@Override
	public void generate(FilesToProcess files) throws IOException {
		if (processor == null) {
			throw new CantHappenException("no processor has been defined");
		}
		processor.process(files);
	}

	@Override
	public void dump() throws IOException {
		FlowDumper dumper = new FlowDumper(root.ensurePlace("fred.flow"));
		for (Flow f : flows) {
			dumper.dump(f);
		}
	}

	@Override
	public void sink() throws IOException {
		for (Sink s : sinks) {
			if (s instanceof WantsFlowMap)
				((WantsFlowMap)s).flowMap(flows);
			for (Flow f : flows) {
				s.flow(f);
			}
			s.render();
		}
	}

	@Override
	public void show() throws IOException {
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
		} else {
			System.out.println("uploads are disabled by ENV");
		}
	}

	public void finish() throws Exception {
		processor.allDone();
		for (Sink s : sinks)
			s.finish();
		for (Finisher f : finishers )
			f.finish();
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

	public GlobalState newGlobalState() {
		return new SolidGlobalState(universe, root, eprepo, finishers, debug, flows);
	}
}
