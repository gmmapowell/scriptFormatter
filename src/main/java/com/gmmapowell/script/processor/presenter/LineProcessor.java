package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;

public interface LineProcessor {
	LineProcessor process(ContinuedLine currline);
	void flush();
}
