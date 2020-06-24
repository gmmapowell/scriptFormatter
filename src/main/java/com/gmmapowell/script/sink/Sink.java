package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.elements.Block;

public interface Sink {
	void title(String title) throws IOException;
	void block(Block block) throws IOException;
	void close() throws IOException;
	void showFinal();
	void upload() throws Exception;
	void brk(Break ad) throws IOException;
}
