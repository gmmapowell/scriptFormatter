package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ModuleActivator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.VarValue;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.processor.ParsingException;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.LineArgsParser;

public abstract class ProsePipeline<T extends CurrentState> implements Processor, ProcessorConfig {
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

	
	protected boolean handleConfiguredSingleLineCommand(T state, String cmd, LineArgsParser p) {
		try {
			ConfigProc cp = lineProcessors.get(cmd);
			if (cp == null)
				return false;
			Constructor<? extends LineCommand> ctor = cp.procclz.getDeclaredConstructor(cp.cfg.getClass(), state.getClass(), LineArgsParser.class);
			LineCommand proc = ctor.newInstance(cp.cfg, state, p);
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
}
