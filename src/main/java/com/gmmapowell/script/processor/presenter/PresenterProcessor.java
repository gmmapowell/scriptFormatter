package com.gmmapowell.script.processor.presenter;

import java.io.IOException;
import java.io.PrintWriter;

import org.flasck.flas.blocker.Blocker;
import org.flasck.flas.errors.ErrorResult;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.sink.Sink;

public class PresenterProcessor implements Processor, ProcessorConfig {
	private final Sink sink;
	private final boolean debug;
	private final BlockDispatcher handler;
	private final Blocker blocker;
	private final ErrorResult errors = new ErrorResult();

	public PresenterProcessor(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		this.sink = sink;
		this.debug = debug;
		String imagedir = options.remove("imagedir");
		if (imagedir == null)
			imagedir = "";
		else if (!imagedir.endsWith("/"))
			imagedir += "/";
		handler = new BlockDispatcher(sink, errors, imagedir);
		this.blocker = new Blocker(errors, handler);
	}
	
	@Override
	public void process(FilesToProcess places) throws IOException {
		for (Place f : places.included()) {
			handler.fileIs(f.name());
			blocker.newFile();
			f.lines((n, s) -> {
				if (!s.startsWith("*")) {
					if (debug)
						System.out.println("ignoring " + s);
					// TODO: surely this should then have a "return;"?
				}
				blocker.present(f.name(), n, reapplyTabs(s));
			});
			blocker.flush();
		}
		errors.showTo(new PrintWriter(System.out), 0);
		sink.render();
	}

	private String reapplyTabs(String s) {
		String prefix = "";
		while (s.length() > 0 && s.charAt(0) == '*') {
			prefix += "\t";
			s = s.substring(1);
		}
		return prefix + s.trim();
	}

	@Override
	public void addScanner(Class<? extends ProcessingScanner> scanner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends ExtensionPoint, Z extends T, Q> void addExtension(Class<T> ep, Creator<Z, Q> impl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends ExtensionPoint, Z extends T> void addExtension(Class<T> ep, Class<Z> impl) {
		// TODO Auto-generated method stub
		
	}
}
