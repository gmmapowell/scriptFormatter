package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ModuleActivator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.VarValue;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.processor.ParsingException;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public abstract class ProsePipeline<T extends CurrentState> implements Processor, ProcessorConfig {
	public class SBLineArgsParser implements LineArgsParser {
		private final T state;
		private final StringBuilder args;

		public SBLineArgsParser(T state, StringBuilder args) {
			this.state = state;
			this.args = args;
		}
		
		@Override
		public boolean hasMore() {
			return ProsePipeline.this.hasMore(state, args);
		}
		
		@Override
		public String readString() {
			return ProsePipeline.this.readString(state, args);
		}
		
		@Override
		public String readArg() {
			return ProsePipeline.this.readArg(state, args);
		}

		@Override
		public void argsDone() {
			assertArgsDone(state, args);
		}
		
		@Override
		public String toString() {
			return args.toString();
		}
	}

	public class ConfigProc {
		private final Class<? extends LineCommand> procclz;
		private final Object cfg;

		public ConfigProc(Class<? extends LineCommand> proc, Object cfg) {
			this.procclz = proc;
			this.cfg = cfg;
		}
	}

	protected final Sink sink;
	protected final ElementFactory ef;
	protected final boolean debug;
	protected final Map<String, ConfigProc> lineProcessors = new TreeMap<>();

	public ProsePipeline(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		this.ef = ef;
		this.sink = sink;
		this.debug = debug;
		
		boolean err = false;
		if (options.containsKey("module")) {
			Iterable<VarValue> modules = options.values("module");
			if (debug)
				System.out.println("modules = " + modules);
			for (VarValue vv : modules) {
				String s = vv.unique();
				if (debug)
					System.out.println("have module " + s);
				try {
					@SuppressWarnings("unchecked")
					Class<? extends ModuleActivator> mclz = (Class<? extends ModuleActivator>) Class.forName(s);
					if (!(ModuleActivator.class.isAssignableFrom(mclz))) {
						System.out.println(s + " was not a module activator");
						err = true;
					}
					Method ctor = mclz.getDeclaredMethod("activate", ProcessorConfig.class, Region.class, VarMap.class);
					ctor.invoke(null, this, root, vv.map());
				} catch (Exception e) {
					System.out.println("could not load module activator class " + s + ": " + e);
					err = true;
				}
			}
			options.delete("module");
		}
		if (err) {
			throw new ConfigException("Could not activate modules");
		}
	}
	
	@Override
	public void installCommand(String cmd, Class<? extends LineCommand> proc, Object cfg) throws ConfigException {
		if (lineProcessors.containsKey(cmd)) {
			throw new ConfigException("cannot install multiple commands for " + cmd);
		}
		lineProcessors.put(cmd, new ConfigProc(proc, cfg));
	}

	@Override
	public void process(FilesToProcess places) throws IOException {
		Map<String, Flow> flows = new TreeMap<>();
		for (Place x : places.included()) {
			T st = begin(flows, x.name());
			x.lines((n, s) -> {
				try {
					if (st.trimLine())
						s = trim(s);
					if (!st.blockquote && s.length() == 0) {
						commitCurrentCommand();
					} else {
						st.line(n);
						handleLine(st, s);
					}
				} catch (ParsingException ex) {
					System.out.println(ex.getMessage());
					System.out.println(" >> " + s);
				} catch (Exception ex) {
					System.out.println("Error encountered processing " + x + " before line " + n);
					if (debug)
						ex.printStackTrace(System.out);
					else
						System.out.println(WrappedException.unwrapAny(ex).toString());
					return;
				}
			});
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

	
	protected boolean handleConfiguredSingleLineCommand(T state, String cmd, StringBuilder args) {
		try {
			ConfigProc cp = lineProcessors.get(cmd);
			if (cp == null)
				return false;
			Constructor<? extends LineCommand> ctor = cp.procclz.getDeclaredConstructor(cp.cfg.getClass(), state.getClass(), LineArgsParser.class);
			LineCommand proc = ctor.newInstance(cp.cfg, state, new SBLineArgsParser(state, args));
			proc.execute();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return true;
		}
	}

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

	protected String readArg(T state, StringBuilder args) {
		if (args == null || args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new ParsingException("cannot read from empty string at " + state.inputLocation());
		char c = args.charAt(0);
		if (c == '\'' || c == '"')
			return readString(state, args);
		for (int i=0;i<args.length();i++) {
			if (Character.isWhitespace(args.charAt(i))) {
				String ret = args.substring(0, i);
				args.delete(0, i);
				return ret;
			}
		}
		String ret = args.toString();
		args.delete(0, args.length());
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
		if (hasMore(state, args))
			throw new RuntimeException("command had junk at end: " + args + " at " + state.inputLocation());
	}
}
