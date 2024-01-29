package com.gmmapowell.script.sink.pdf;

import java.util.List;

import com.gmmapowell.script.flow.StyledToken;

public interface Outcome {

	boolean forcedNewLine();

	List<StyledToken> replay();

}
