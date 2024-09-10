package com.gmmapowell.script.modules.processors.presenter;

import java.util.ArrayList;
import java.util.List;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.blocker.BlockConsumer;
import org.flasck.flas.errors.ErrorReporter;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.FlowMap;

public class BlockDispatcher implements BlockConsumer {
	private final ErrorReporter errors;
	private final PresenterGlobals global;
	private final FlowMap flows;
	private final String imagedir;
	private final List<LineProcessor> stack = new ArrayList<>();

	public BlockDispatcher(ErrorReporter errors, PresenterGlobals global, FlowMap flows, String imagedir) {
		this.errors = errors;
		this.global = global;
		this.flows = flows;
		this.imagedir = imagedir;
	}

	public void fileIs(String name) {
	}
	
	@Override
	public void newFile() {
		this.flush();
		stack.clear();
		
		this.stack.add(new PresentationProcessor(errors, global, flows, imagedir));
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
