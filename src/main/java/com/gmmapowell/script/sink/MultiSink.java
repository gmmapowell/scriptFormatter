package com.gmmapowell.script.sink;

import java.io.IOException;
import java.util.List;

import com.gmmapowell.script.elements.Block;

public class MultiSink implements Sink {
	private final List<Sink> sinks;

	public MultiSink(List<Sink> sinks) {
		this.sinks = sinks;
	}

	@Override
	public void title(String title) throws IOException {
		for (Sink s : sinks)
			s.title(title);
	}

	@Override
	public void block(Block block) throws IOException {
		for (Sink s : sinks)
			s.block(block);
	}

	@Override
	public void close() throws IOException {
		for (Sink s : sinks)
			s.close();
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