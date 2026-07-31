package com.gmmapowell.script.sink.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.WantsFlowMap;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.sink.Sink;

public class GithubSink implements Sink, WantsFlowMap {
	private Region root;
	private Region dir;
	private final List<Flow> flows = new ArrayList<>();
	private FlowMap map;
	
	public GithubSink(Region root, Region dir, boolean debug) throws IOException {
		this.root = root;
		this.dir = dir;
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public void flowMap(FlowMap flows) {
		map = flows;
	}

	@Override
	public void flow(Flow flow) {
		System.out.println("have flow " + flow.name);
		flows.add(flow);
	}
	
	@Override
	public void render() {
	}

	@Override
	public void showFinal() {
	}

	@Override
	public void upload() throws Exception {
	}

	@Override
	public void finish() throws Exception {
	}
}
