package com.gmmapowell.script.sink.pdf;

import java.util.List;

public interface Outcome {

	boolean forcedNewLine();

	List<StyledToken> replay();

}
