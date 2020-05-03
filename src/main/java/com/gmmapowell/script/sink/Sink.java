package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.elements.Block;

public interface Sink {

	void showFinal();

	void block(Block block) throws IOException;

	void close() throws IOException;

}
