package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.pdf.Stock;
import com.gmmapowell.script.elements.Block;

public interface Sink {
	void flow(Flow flow);
	void render(Stock stock) throws IOException;
	void showFinal();
	void upload() throws Exception;
	@Deprecated
	void block(Block block) throws IOException;
	@Deprecated
	void brk(Break ad) throws IOException;
}
