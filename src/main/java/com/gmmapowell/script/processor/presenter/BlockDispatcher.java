package com.gmmapowell.script.processor.presenter;

import java.util.ArrayList;
import java.util.List;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.blocker.BlockConsumer;
import org.flasck.flas.errors.ErrorReporter;
import org.zinutils.exceptions.NotImplementedException;

public class BlockDispatcher implements BlockConsumer {
	private List<LineProcessor> stack;
	private final ErrorReporter errors;
	private final PresentationMapper mapper;
	private String nextName;

	public BlockDispatcher(ErrorReporter errors, PresentationMapper mapper) {
		this.errors = errors;
		this.mapper = mapper;
	}

	public void fileIs(String name) {
		this.nextName = name;
	}
	
	@Override
	public void newFile() {
		stack = new ArrayList<>();
		this.stack.add(new PresentationProcessor(errors, mapper, nextName));
	}

	@Override
	public void comment(String text) {
		
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
