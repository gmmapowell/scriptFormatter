package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class BlogPipeline implements Processor {

	private final Sink sink;

	public BlogPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		this.sink = sink;
	}
	
	@Override
	public void process(FilesToProcess files) throws IOException {
		sink.title("A More Functional Database");
	}
}
