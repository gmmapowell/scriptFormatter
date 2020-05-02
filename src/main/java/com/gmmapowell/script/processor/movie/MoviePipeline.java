package com.gmmapowell.script.processor.movie;

import java.util.Map;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class MoviePipeline implements Processor {
	private final String dramatis;

	public MoviePipeline(Sink outputTo, Map<String, String> options) throws ConfigException {
		this.dramatis = options.remove("dramatis");
		if (dramatis == null)
			throw new ConfigException("There is no definition of dramatis");
	}
}
