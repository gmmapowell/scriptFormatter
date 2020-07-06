package com.gmmapowell.script.elements.block;

import java.util.List;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.movie.MultiBlock;
import com.gmmapowell.script.processor.prose.CommentaryBreak;

public class BlockishElementFactory implements ElementFactory {

	@Override
	public SpanBlock block(String format) {
		return new TextBlock(format);
	}
	
	@Override
	public Span html(String text) {
		return new HTMLSpan(text);
	}

	@Override
	public Span span(String format, String text) {
		return new TextSpan(format, text);
	}

	@Override
	public Span lspan(List<String> formats, String text) {
		return new TextSpan(formats, text);
	}

	@Override
	public Group group() {
		return new MultiBlock();
	}

	@Override
	public Break adbreak() {
		return new BoxyAdBreak();
	}

	@Override
	public Break commentarybreak() {
		return new CommentaryBreak();
	}

}
