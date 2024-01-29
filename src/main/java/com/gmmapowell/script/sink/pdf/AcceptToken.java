package com.gmmapowell.script.sink.pdf;

import java.util.List;

import com.gmmapowell.script.flow.StyledToken;

public class AcceptToken implements Outcome {
	private final Float onl;
	private final List<StyledToken> left;

	public AcceptToken() {
		this.onl = null;
		this.left = null;
	}
	
	public AcceptToken(Float onl, List<StyledToken> left) {
		this.onl = onl;
		this.left = left;
	}

	public boolean forcedNewLine() {
		return onl != null;
	}

	public float getOverflow() {
		return onl;
	}
	
	@Override
	public List<StyledToken> replay() {
		return left;
	}
}
