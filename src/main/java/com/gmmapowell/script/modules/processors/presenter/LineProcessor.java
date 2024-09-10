package com.gmmapowell.script.modules.processors.presenter;

import org.flasck.flas.blockForm.ContinuedLine;

public interface LineProcessor {
	LineProcessor process(ContinuedLine currline);
	void flush();
}
