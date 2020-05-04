package com.gmmapowell.script.elements.block;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.movie.MultiBlock;

public class BlockishElementFactory implements ElementFactory {

	@Override
	public SpanBlock block(String format) {
		return new TextBlock(format);
	}
	
	@Override
	public Span span(String format, String text) {
		return new TextSpan(format, text);
	}

	@Override
	public Group group() {
		return new MultiBlock();
	}

}
