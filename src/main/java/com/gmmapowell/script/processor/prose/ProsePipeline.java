package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
			T st = begin(x.getName());
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(x))) {
				String s;
				while ((s = lnr.readLine()) != null) {
					try {
						if (st.trimLine())
							s = trim(s);
						if (s.length() == 0) {
							endBlock(st);
						} else {
							st.line(lnr.getLineNumber());
							handleLine(st, s);
						}
					} catch (Exception ex) {
						System.out.println("Error encountered processing " + x + " before line " + lnr.getLineNumber());
						System.out.println(ex.toString());
						if (debug)
							ex.printStackTrace();
						st.curr = null;
					}
				}
			}
			try {
				endBlock(st);
			} catch (Exception ex) {
				System.out.println("Exception processing block at end of " + x + ": " + ex);
				if (debug)
					ex.printStackTrace();
			}
			fileDone();
			try {
				sink.fileEnd();
			} catch (Exception ex) {
				System.out.println("Exception processing file end of " + x + ": " + ex);
				if (debug)
					ex.printStackTrace();
			}
		}
		done();
		sink.close();
	}

	protected void endBlock(T st) throws IOException {
		if (st.curr != null) {
			sink.block(st.curr);
			st.curr = null;
		}
	}

	protected abstract T begin(String file);
	protected abstract void handleLine(T state, String s) throws IOException;
	protected void fileDone() {}
	protected void done() {}

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

	protected String readString(T state, StringBuilder args) {
		if (args == null || args.length() == 0)
			throw new RuntimeException("cannot read from empty string at " + state.location());
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new RuntimeException("cannot read from empty string at " + state.location());
		char c = args.charAt(0);
		if (c != '\'' && c != '"')
			throw new RuntimeException("unquoted string at " + state.location());
		args.delete(0, 1);
		String ret = null;
		for (int i=0;i<args.length();i++) {
			if (args.charAt(i) == c) {
				ret = args.substring(0, i);
				args.delete(0, i+1);
				break;
			}
		}
		if (ret == null)
			throw new RuntimeException("unterminated string at " + state.location());
		return ret;
	}

	protected Map<String, String> readParams(T state, StringBuilder args, String... allowedStrings) {
		Map<String, String> ret = new TreeMap<>();
		if (args == null)
			return ret;
		List<String> allowed = Arrays.asList(allowedStrings);
		while (args.length() > 0) {
			while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
				args.delete(0, 1);
			if (args.length() == 0)
				break;
			int j=0;
			while (j < args.length() && args.charAt(j) != '=')
				j++;
			if (j == args.length())
				throw new RuntimeException("needed =");
			String var = args.substring(0, j);
			args.delete(0, j+1);
			if (ret.containsKey(var))
				throw new RuntimeException("duplicate definition of " + var);
			else if (!allowed.contains(var))
				throw new RuntimeException("unexpected definition of " + var + "; allowed = " + allowed);
			else
				ret.put(var, readString(state, args)); 
		}
		return ret;
	}
}
