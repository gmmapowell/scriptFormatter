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
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class ConfiguredProcessor implements Processor, ProcessorConfig {
	private Class<? extends ProcessingHandler> defaultHandler;
	private Class<? extends ProcessingHandler> blankHandler;
	private List<Class<? extends ProcessingScanner>> scanners = new ArrayList<>();
	private ExtensionPointRepo eprepo;
	private ExtensionPointRepo local = new CreatorExtensionPointRepo();

	public ConfiguredProcessor(ExtensionPointRepo eprepo, Region root, BlockishElementFactory blockishElementFactory, Sink sink, VarMap vars, boolean debug) {
		this.eprepo = eprepo;
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
	
	@Override
	public <T, Z extends T, Q> void addExtension(Class<T> ep, Creator<Z, Q> impl) {
		local.bindExtensionPoint(ep, impl);
	}

	@Override
	public <T, Z extends T> void addExtension(Class<T> ep, Class<Z> impl) {
		local.bindExtensionPoint(ep, impl);
	}

	@Override
	public void process(FilesToProcess places) throws IOException {
		// TODO: create a "bigger" state (which persists across input files)
		for (Place x : places.included()) {
			ConfiguredState state = new ConfiguredState(eprepo);
			List<ProcessingScanner> all = createScannerList(state);

			// Each of the scanners gets a chance to act
			x.lines((n, s) -> {
				System.out.print("# " + n + ": " + (s + "...........").substring(0, 10) + ":: ");
				for (ProcessingScanner scanner : all) {
					if (scanner.handleLine(trim(s)))
						return;
				}
				throw new CantHappenException("the default scanner at least should have fired");
			});
			for (ProcessingScanner scanner : all)
				scanner.placeDone();
		}
	}

	private List<ProcessingScanner> createScannerList(ConfiguredState state) {
		// Create a list of scanners so that the one defined last is tried first,
		// and the default handler only applies if none of the others do
		List<ProcessingScanner> all = new ArrayList<ProcessingScanner>();
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
