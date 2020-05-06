package com.gmmapowell.script.processor;

import java.io.IOException;

import com.gmmapowell.script.FilesToProcess;

public interface Processor {

	void process(FilesToProcess files) throws IOException;

}
