package com.gmmapowell.script.sink;

import java.io.IOException;
import java.util.List;

import com.gmmapowell.script.flow.Flow;

public class MultiSink implements Sink {
	private final List<Sink> sinks;

	public MultiSink(List<Sink> sinks) {
		this.sinks = sinks;
	}

	@Override
	public void prepare() throws Exception {
		for (Sink s : sinks)
			s.prepare();
	}
	
	@Override
	public void flow(Flow flow) {
		for (Sink s : sinks)
			s.flow(flow);
	}

	@Override
	public void render() throws IOException {
		for (Sink s : sinks)
			s.render();
	}

	@Override
	public void showFinal() {
		for (Sink s : sinks)
			s.showFinal();
	}

	@Override
	public void upload() throws Exception {
		for (Sink s : sinks)
			s.upload();
	}
}
