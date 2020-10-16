package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.pdf.Stock;

public interface Sink {
	void flow(Flow flow);
	void render(Stock stock) throws IOException;
	void showFinal();
	void upload() throws Exception;
}
