package com.gmmapowell.script.sink.pdf;

import java.util.List;

public class PendingToken implements Outcome {
	public PendingToken() {
	}

	@Override
	public boolean forcedNewLine() {
		return false;
	}

	@Override
	public List<StyledToken> replay() {
		return null;
	}
}
