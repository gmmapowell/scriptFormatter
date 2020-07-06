package com.gmmapowell.script.processor.presenter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Map;

import org.flasck.flas.blocker.Blocker;
import org.flasck.flas.errors.ErrorResult;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class PresenterPipeline implements Processor {
	private final Sink sink;
	private final Blocker blocker;
	private final ErrorResult errors = new ErrorResult();
	private final boolean debug;

	public PresenterPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		this.debug = debug;
		this.blocker = new Blocker(errors, new BlockDispatcher(errors));
		this.sink = sink;
	}
	
	@Override
	public void process(FilesToProcess files) throws IOException {
		for (File f : files.included()) {
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
			try {
				sink.fileEnd();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sink.close();
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
}
