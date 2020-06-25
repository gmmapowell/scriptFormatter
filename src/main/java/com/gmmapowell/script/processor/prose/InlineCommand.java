package com.gmmapowell.script.processor.prose;

import java.io.IOException;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.sink.Sink;

public interface InlineCommand {
	void execute(Sink sink, ElementFactory ef) throws IOException;
}
