package com.gmmapowell.script.processor.prose;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.sink.Sink;

public abstract class AtProcessor<T extends AtState> extends ProseProcessor<T> {

	public AtProcessor(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}
}
