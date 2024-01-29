package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.processor.ParsingException;
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
		Map<String, Flow> flows = new TreeMap<>();
		for (File x : files.included()) {
			T st = begin(flows, x.getName());
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(x))) {
				String s;
				while ((s = lnr.readLine()) != null) {
					try {
						if (st.trimLine())
							s = trim(s);
						if (!st.blockquote && s.length() == 0) {
							commitCurrentCommand();
						} else {
							st.line(lnr.getLineNumber());
							handleLine(st, s);
						}
					} catch (ParsingException ex) {
						System.out.println(ex.getMessage());
						System.out.println(" >> " + s);
					} catch (Exception ex) {
						System.out.println("Error encountered processing " + x + " before line " + lnr.getLineNumber());
						if (debug)
							ex.printStackTrace(System.out);
						 else
							System.out.println(WrappedException.unwrapAny(ex).toString());
						return;
					}
				}
			}
			try {
				commitCurrentCommand();
			} catch (Exception ex) {
				System.out.println("Exception processing block at end of " + x + ": " + ex);
				if (debug)
					ex.printStackTrace();
			}
			fileDone();
		}
		for (Entry<String, Flow> e : flows.entrySet()) {
			sink.flow(e.getValue());
		}
		done();
		sink.render();
		postRender();
	}


	protected abstract void commitCurrentCommand() throws IOException;
	protected abstract T begin(Map<String, Flow> flows, String file);
	protected abstract void handleLine(T state, String s) throws IOException;
	protected void fileDone() {}
	protected void done() throws IOException {}
	protected void postRender() {}

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

	protected boolean hasMore(T state, StringBuilder args) {
		if (args == null || args.length() == 0)
			return false;
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		return args.length() > 0;
	}

	protected String readString(T state, StringBuilder args) {
		if (args == null || args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		char c = args.charAt(0);
		if (c != '\'' && c != '"')
			throw new ParsingException("unquoted string at " + state.inputLocation());
		args.delete(0, 1);
		String ret = null;
		for (int i=0;i<args.length();i++) {
			if (args.charAt(i) == c) {
				if (i+1<args.length() && args.charAt(i+1) == c) {
					args.delete(i, i+1);
					continue;
				}
				ret = args.substring(0, i);
				args.delete(0, i+1);
				break;
			}
		}
		if (ret == null)
			throw new ParsingException("unterminated string at " + state.inputLocation());
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
	
	protected void assertArgsDone(T state, StringBuilder args) {
		if (args.toString().trim().length() > 0)
			throw new RuntimeException("command had junk at end: " + args + " at " + state.inputLocation());
	}
}
