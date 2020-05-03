package com.gmmapowell.script.elements.block;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;

public class BlockishElementFactory implements ElementFactory {

	@Override
	public Block block(String format) {
		return new TextBlock(format);
	}
	
	@Override
	public Span span(String format, String text) {
		return new TextSpan(format, text);
	}

}
