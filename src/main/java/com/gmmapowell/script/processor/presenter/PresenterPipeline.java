package com.gmmapowell.script.processor.presenter;

import java.io.IOException;
import java.io.PrintWriter;

import org.flasck.flas.blocker.Blocker;
import org.flasck.flas.errors.ErrorResult;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.kNodes.Galaxy;
import com.gmmapowell.script.presenter.nodes.Presentation;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class PresenterPipeline implements Processor, PresentationMapper {
	private final Region root;
	private final boolean debug;
	private final BlockDispatcher handler;
	private final Blocker blocker;
	private final ErrorResult errors = new ErrorResult();

	public PresenterPipeline(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		this.root = root;
		this.debug = debug;
		String imagedir = options.remove("imagedir");
		if (imagedir == null)
			imagedir = "";
		else if (!imagedir.endsWith("/"))
			imagedir += "/";
		handler = new BlockDispatcher(errors, this, imagedir);
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
	public void present(Presentation presentation) {
		Galaxy<Slide> g = new Galaxy<Slide>(presentation.slides());
		Place f = root.ensurePlace(FileUtils.ensureExtension(presentation.name, ".json"));
		Place m = root.ensurePlace(FileUtils.ensureExtension(presentation.name, ".meta"));
		try {
			g.asJson(f.writer());
			g.writeMeta(m.writer());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}
