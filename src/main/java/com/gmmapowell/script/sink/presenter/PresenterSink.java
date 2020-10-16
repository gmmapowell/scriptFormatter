package com.gmmapowell.script.sink.presenter;

import java.io.File;
import java.io.IOException;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.Stock;

public class PresenterSink implements Sink {
	public PresenterSink(File root, String output, boolean wantOpen, String upload, boolean debug) throws IOException {
	}

	@Override
	public void flow(Flow flow) {
	}
	
	@Override
	public void render(Stock stock) {
	}

	@Override
	public void block(Block block) throws IOException {
	}

	@Override
	public void brk(Break ad) throws IOException {
	}

	@Override
	public void showFinal() {
	}

	@Override
	public void upload() throws Exception {
	}
}
