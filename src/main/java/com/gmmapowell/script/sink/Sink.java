package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.flow.Flow;

public interface Sink {
	void prepare() throws Exception;
	void flow(Flow flow);
	void render() throws IOException;
	default void flush() throws IOException {};
	void showFinal();
	void upload() throws Exception;
	void finish() throws Exception;
}
