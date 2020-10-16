package com.gmmapowell.script.processor.presenter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import org.flasck.flas.blocker.Blocker;
import org.flasck.flas.errors.ErrorResult;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.kNodes.Galaxy;
import com.gmmapowell.script.presenter.nodes.Presentation;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class PresenterPipeline implements Processor, PresentationMapper {
	private final File root;
	private final boolean debug;
	private final BlockDispatcher handler;
	private final Blocker blocker;
	private final Sink sink;
	private final ErrorResult errors = new ErrorResult();

	public PresenterPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		this.root = root;
		this.debug = debug;
		String imagedir = options.remove("imagedir");
		if (imagedir == null)
			imagedir = "";
		else if (!imagedir.endsWith("/"))
			imagedir += "/";
		this.sink = sink;
		handler = new BlockDispatcher(errors, this, imagedir);
		this.blocker = new Blocker(errors, handler);
	}
	
	@Override
	public void process(FilesToProcess files) throws IOException {
		for (File f : files.included()) {
			handler.fileIs(f.getName());
			blocker.newFile();
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(f))) {
				String s;
				while ((s = lnr.readLine()) != null) {
					if (!s.startsWith("*")) {
						if (debug)
							System.out.println("ignoring " + s);
					}
					blocker.present(f.getName(), lnr.getLineNumber(), reapplyTabs(s));
				}
			}
			blocker.flush();
//			try {
//				sink.fileEnd();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
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
		File f = FileUtils.ensureExtension(new File(root, presentation.name), ".json");
		try (Writer w = new FileWriter(f)) {
			g.asJson(w);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}
