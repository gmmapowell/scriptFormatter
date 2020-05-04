package com.gmmapowell.script.processor.movie;

import java.io.IOException;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.sink.Sink;

public class Formatter {
	private final ElementFactory factory;
	private final Sink sink;
	private final boolean debug;
	private Group speakingBlock;

	public Formatter(ElementFactory factory, Sink outputTo, boolean debug) {
		this.factory = factory;
		this.sink = outputTo;
		this.debug = debug;
	}

	public void title(String title) throws IOException {
		if (debug)
			System.out.println("# " + title);
		SpanBlock block = factory.block("title");
		Span span = factory.span(null, title.toUpperCase());
		block.addSpan(span);
		sink.block(block);
	}

	public void slug(String slug) throws IOException {
		if (debug)
			System.out.println("! " + slug);
		SpanBlock block = factory.block("slug");
		Span span = factory.span(null, slug.toUpperCase());
		block.addSpan(span);
		sink.block(block);
	}

	public void speaker(String speaker) throws IOException {
		if (debug)
			System.out.println("| " + speaker);
		SpanBlock block = factory.block("speaker");
		Span span = factory.span(null, speaker);
		block.addSpan(span);
		speakingBlock = factory.group();
		speakingBlock.addBlock(block);
	}

	public void direction(String text) throws IOException {
		if (debug)
			System.out.println("| " + text);
		SpanBlock block = factory.block("direction");
		Span span = factory.span(null, text);
		block.addSpan(span);
		speakingBlock.addBlock(block);
	}

	public void speech(String speech) throws IOException {
		if (debug)
			System.out.println("<< " + speech);
		SpanBlock block = factory.block("speech");
		Span span = factory.span(null, speech);
		block.addSpan(span);
		speakingBlock.addBlock(block);
	}

	public void endSpeech() throws IOException {
		sink.block(speakingBlock);
	}

	public void scene(String text) throws IOException {
		if (debug)
			System.out.println("... " + text);
		SpanBlock block = factory.block("scene");
		Span span = factory.span(null, text);
		block.addSpan(span);
		sink.block(block);
	}

	public void close() throws IOException {
		sink.close();
	}

}
