package com.gmmapowell.script.processor;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingHandler;

public class NewParaProcessor implements ProcessingHandler {
	public NewParaProcessor(ConfiguredState state) {
	}

	@Override
	public void process(String s) {
		System.out.println("blank line");
	}
}
