package com.gmmapowell.script.modules.processors.presenter;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingHandler;

public class IgnoreLine implements ProcessingHandler {

	public IgnoreLine(ConfiguredState ign) {
	}

	@Override
	public void process(String s) {
		// as the name implies, this does nothing
		System.out.println("ignore...");
	}

}
