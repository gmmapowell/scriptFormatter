package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class BlogPipeline implements Processor {

	public BlogPipeline(File root, ElementFactory ef, Sink outputTo, Map<String, String> options, boolean debug) throws ConfigException {
	}
	
	@Override
	public void process(FilesToProcess files) {
	}

}
