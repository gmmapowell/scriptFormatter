package com.gmmapowell.script.processor.prose;

import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public class ArticleState extends AtState {
	public boolean blockquote;

	public ArticleState(Map<String, Flow> flows, String file) {
		super(flows);
		this.processingFile(file);
	}

	@Override
	public String formatAs() {
		return "preformatted";
	}
	
	@Override
	protected boolean trimLine() {
		return !blockquote;
	}
}
