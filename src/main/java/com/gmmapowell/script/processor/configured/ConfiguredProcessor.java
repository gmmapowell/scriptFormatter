package com.gmmapowell.script.processor.configured;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.CreatorExtensionPointRepo;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.Processor;

public class ConfiguredProcessor implements Processor, ProcessorConfig {
	private final GlobalState global;
	private final ExtensionPointRepo local;
	private final boolean joinspace;
	private Class<? extends ProcessingHandler> defaultHandler;
	private Class<? extends ProcessingHandler> blankHandler;
	private final List<Class<? extends ProcessingScanner>> scanners = new ArrayList<>();
	private final List<LifecycleObserver> observers = new ArrayList<>();

	public ConfiguredProcessor(GlobalState global, Region root, BlockishElementFactory blockishElementFactory, VarMap vars, boolean debug) {
		this.global = global;
		this.local = new CreatorExtensionPointRepo(global.extensions());
		// TODO: this should be a negotiation between the configlistener and an (initialized) ConfiguredProcessor
		if (vars != null) {
			String joinspace = vars.remove("joinspace");
			this.joinspace = "true".equals(joinspace);
		} else
			this.joinspace = false;
	}
	
	public GlobalState global() {
		return global;
	}
	
	public void setDefaultHandler(Class<? extends ProcessingHandler> handler) {
		defaultHandler = handler;
	}
	
	public void setBlankHandler(Class<? extends ProcessingHandler> handler) {
		blankHandler = handler;
	}
	
	public void addScanner(Class<? extends ProcessingScanner> scanner) {
		scanners.add(scanner);
	}
	
	public void lifecycleObserver(LifecycleObserver observer) {
		observers.add(observer);
	}
	
	@Override
	public <T extends ExtensionPoint, Z extends T, Q> void addExtension(Class<T> ep, Creator<Z, Q> impl) {
		local.bindExtensionPoint(ep, impl);
	}

	@Override
	public <T extends ExtensionPoint, Z extends T> void addExtension(Class<T> ep, Class<Z> impl) {
		local.bindExtensionPoint(ep, impl);
	}
	
	@Override
	public void process(FilesToProcess places) throws IOException {
		// TODO: create a "bigger" state (which persists across input files)
		Fluency fluency = new Fluency(global);
		for (Place x : places.included()) {
			System.out.println("Handling " + x.name());
			ConfiguredState state = new ConfiguredState(global, local, fluency, joinspace, x);
			List<ProcessingScanner> all = createScannerList(state);

			for (LifecycleObserver o : observers)
				o.newPlace(state, x);

			// Each of the scanners gets a chance to act
			x.lines((n, s) -> {
				state.line(n);
				String trimmed = trim(s);
				for (ProcessingScanner scanner : all)
					scanner.closeIfNotContinued(scanner.wantTrimmed() ? trimmed : s);
				System.out.print("# " + n + ": " + (s + "...........").substring(0, 10) + ":: ");
				for (ProcessingScanner scanner : all) {
					if (scanner.handleLine(scanner.wantTrimmed() ? trimmed : s))
						return;
				}
				throw new CantHappenException("the default scanner at least should have fired");
			});
			for (ProcessingScanner scanner : all)
				scanner.closeIfNotContinued(null);
			for (ProcessingScanner scanner : all)
				scanner.placeDone();
			for (LifecycleObserver o : observers)
				o.placeDone(state);
		}
		for (LifecycleObserver o : observers)
			o.processingDone();
	}

	@Override
	public void allDone() {
		for (LifecycleObserver o : observers)
			o.allDone(global);
	}
	
	private List<ProcessingScanner> createScannerList(ConfiguredState state) {
		// Create a list of scanners so that the one defined last is tried first,
		// and the default handler only applies if none of the others do
		List<ProcessingScanner> all = new ArrayList<ProcessingScanner>();
		if (defaultHandler == null) {
			throw new CantHappenException("the processor config must specify a defaultHandler");
		}
		if (blankHandler == null) {
			throw new CantHappenException("the processor config must specify a blankHandler");
		}
		all.add(0, new AlwaysScanner(Reflection.create(defaultHandler, state)));
		all.add(0, new BlankScanner(Reflection.create(blankHandler, state)));
		for (Class<? extends ProcessingScanner> c : scanners) {
			all.add(0, Reflection.create(c, state));
		}
		return all;
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
