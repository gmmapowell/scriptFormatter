package com.gmmapowell.script.processor.configured;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class ConfiguredProcessor implements Processor {
	private Class<? extends ProcessingHandler> defaultHandler;
	private Class<? extends ProcessingHandler> blankHandler;
	private List<Class<? extends ProcessingScanner>> scanners = new ArrayList<>();

	public ConfiguredProcessor(Region root, BlockishElementFactory blockishElementFactory, Sink sink, VarMap vars, boolean debug) {
		// TODO Auto-generated constructor stub
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
	public void process(FilesToProcess places) throws IOException {
		// create a bigger state
		for (Place x : places.included()) {
			System.out.println("process " + x);
			ConfiguredState state = new ConfiguredState();
			
			// Create a list of scanners so that the one defined last is tried first,
			// and the default handler only applies if none of the others do
			List<ProcessingScanner> all = new ArrayList<ProcessingScanner>();
			all.add(0, new AlwaysScanner(Reflection.create(defaultHandler, state)));
			all.add(0, new BlankScanner(Reflection.create(blankHandler, state)));
			for (Class<? extends ProcessingScanner> c : scanners) {
				all.add(0, Reflection.create(c, state));
			}

			// Each of the scanners gets a chance to act
			x.lines((n, s) -> {
				System.out.print("# " + n + ": " + (s + "...........").substring(0, 10) + ":: ");
				for (ProcessingScanner scanner : all) {
					if (scanner.handleLine(s))
						return;
				}
				throw new CantHappenException("the default scanner at least should have fired");
			});
			// TODO: tell all of the scanners we are done
		}
		// and we are done
	}
}
