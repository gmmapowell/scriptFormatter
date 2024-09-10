package com.gmmapowell.script.modules.processors.presenter;

import java.io.IOException;
import java.io.PrintWriter;

import org.flasck.flas.blocker.Blocker;
import org.flasck.flas.errors.ErrorResult;
import org.zinutils.exceptions.WrappedException;
import org.zinutils.graphs.DirectedCyclicGraph;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.processor.presenter.BlockDispatcher;

public class PresenterGlobals {
	private ErrorResult errors;
	private BlockDispatcher handler;
	private Blocker blocker;
	private Place curr;
	private String lastSlide;
	private DirectedCyclicGraph<String> graph = new DirectedCyclicGraph<>();
	
	public void configure(Region root, FlowMap flows, String imagedir) {
		errors = new ErrorResult();
		handler = new BlockDispatcher(errors, this, flows, imagedir);
		this.blocker = new Blocker(errors, handler);
		flows.bindOOB("links", graph);
	}

	public void newPlace(Place x) {
		this.curr = x;
		handler.fileIs(x.name());
		blocker.newFile();
	}

	public void placeDone() {
		blocker.flush();
	}

	public void allDone() {
		try {
			errors.showTo(new PrintWriter(System.out), 0);
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	public void present(int n, String s) {
		blocker.present(curr.name(), n, s);
	}

	public void nextSlide(String flowName) {
		graph.ensure(flowName);
		if (lastSlide != null) {
			graph.ensureLink(lastSlide, flowName);
		}
		lastSlide = flowName;
	}
}
