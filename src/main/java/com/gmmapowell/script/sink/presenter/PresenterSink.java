package com.gmmapowell.script.sink.presenter;

import java.io.File;
import java.io.IOException;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.sink.Sink;

public class PresenterSink implements Sink {
	public PresenterSink(File root, String output, boolean wantOpen, String upload, boolean debug) throws IOException {
	}
	
	@Override
	public void title(String title) throws IOException {
	}

	@Override
	public void block(Block block) throws IOException {
	}

	@Override
	public void brk(Break ad) throws IOException {
	}

	@Override
	public void fileEnd() throws Exception {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void showFinal() {
	}

	@Override
	public void upload() throws Exception {
	}
}
