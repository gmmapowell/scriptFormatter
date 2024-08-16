package com.gmmapowell.script.processor.presenter;

import java.util.ArrayList;
import java.util.List;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.blocker.BlockConsumer;
import org.flasck.flas.errors.ErrorReporter;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.sink.Sink;

public class BlockDispatcher implements BlockConsumer {
	private final Sink sink;
	private final List<LineProcessor> stack = new ArrayList<>();
	private final ErrorReporter errors;
	private final String imagedir;

	public BlockDispatcher(Sink sink, ErrorReporter errors, String imagedir) {
		this.sink = sink;
		this.errors = errors;
		this.imagedir = imagedir;
	}

	public void fileIs(String name) {
	}
	
	@Override
	public void newFile() {
		this.flush();
		stack.clear();
		
		// TODO: this is either wrong or just inconsistent with everything else we do
		// All the source files should be gathered together, and then one image shown
		// in the "sink"
		this.stack.add(new PresentationProcessor(sink, errors, imagedir));
	}

	@Override
	public void comment(InputPosition pos, String text) {
		
	}

	@Override
	public void line(int depth, ContinuedLine currline) {
//		System.out.println("presenting " + depth + " " + currline.text() + " stack = " + stack);
		while (stack.size() > depth)
			stack.remove(0).flush();
		if (depth > stack.size())
			throw new NotImplementedException(); 
		LineProcessor ind = stack.get(0).process(currline);
		stack.add(0, ind);
	}

	@Override
	public void flush() {
		while (!stack.isEmpty()) {
			stack.remove(0).flush();
		}
	}
}
