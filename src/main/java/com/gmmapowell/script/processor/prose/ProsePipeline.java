package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public abstract class ProsePipeline<T extends CurrentState> implements Processor {
	protected final Sink sink;
	protected final ElementFactory ef;
	protected final boolean debug;

	public ProsePipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		this.ef = ef;
		this.sink = sink;
		this.debug = debug;
	}
	
	@Override
	public void process(FilesToProcess files) throws IOException {
		for (File x : files.included()) {
			sink.title(x.getName().replace(".txt", ""));
			T st = begin();
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(x))) {
				String s;
				while ((s = lnr.readLine()) != null) {
					try {
						s = trim(s);
						if (s.length() == 0) {
							endBlock(st);
						} else
							handleLine(st, s);
					} catch (Exception ex) {
						System.out.println("Error encountered processing " + x + " before line " + lnr.getLineNumber());
						System.out.println(ex.toString());
						if (debug)
							ex.printStackTrace();
						st.curr = null;
					}
				}
			}
			if (st.curr != null)
				sink.block(st.curr);
		}
		sink.close();
	}

	protected void endBlock(T st) throws IOException {
		if (st.curr != null) {
			sink.block(st.curr);
			st.curr = null;
		}
	}

	protected abstract T begin();
	protected abstract void handleLine(T state, String s) throws IOException;

	private String trim(String s) {
		StringBuilder sb = new StringBuilder(s.trim());
		for (int i=0;i<sb.length();) {
			if (sb.charAt(i) == '\uFEFF')
				sb.delete(i, i+1);
			else
				i++;
		}
		return sb.toString().trim();
	}
}
