package com.gmmapowell.script.processor.prose;

import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public abstract class AtState extends CurrentState {
	public DocCommand cmd;
	public LineCommand inline;

	public AtState(Map<String, Flow> flows) {
		super(flows);
	}

	@Override
	public void line(int lineNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public String inputLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
