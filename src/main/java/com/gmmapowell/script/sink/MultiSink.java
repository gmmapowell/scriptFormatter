package com.gmmapowell.script.sink;

import java.io.IOException;
import java.util.List;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.pdf.Stock;

public class MultiSink implements Sink {
	private final List<Sink> sinks;

	public MultiSink(List<Sink> sinks) {
		this.sinks = sinks;
	}

	@Override
	public void flow(Flow flow) {
		for (Sink s : sinks)
			s.flow(flow);
	}

	@Override
	public void render(Stock stock) throws IOException {
		for (Sink s : sinks)
			s.render(stock);
	}

	@Override
	public void block(Block block) throws IOException {
		for (Sink s : sinks)
			s.block(block);
	}

	@Override
	public void brk(Break ad) throws IOException {
		for (Sink s: sinks) {
			s.brk(ad);
		}
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
