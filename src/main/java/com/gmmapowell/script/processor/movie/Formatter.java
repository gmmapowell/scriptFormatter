package com.gmmapowell.script.processor.movie;

import java.io.IOException;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class Formatter {
	private final ElementFactory factory;
	private final Sink sink;
	private final boolean debug;
	private Group slugBlock;
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
		slugBlock = factory.group();
		SpanBlock block = factory.block("slug");
		Span span = factory.span(null, slug.toUpperCase());
		block.addSpan(span);
		slugBlock.addBlock(block);
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
		ProcessingUtils.addSpans(factory, block, speech);
		speakingBlock.addBlock(block);
	}

	public void endSpeech() throws IOException {
		if (slugBlock != null) {
			slugBlock.addBlock(speakingBlock);
			sink.block(slugBlock);
			slugBlock = null;
		} else
			sink.block(speakingBlock);
		speakingBlock = null;
	}

	public void scene(String text) throws IOException {
		if (debug)
			System.out.println("... " + text);
		SpanBlock block = factory.block("scene");
		Span span = factory.span(null, text);
		block.addSpan(span);
		if (slugBlock != null) {
			slugBlock.addBlock(block);
			sink.block(slugBlock);
			slugBlock = null;
		} else
			sink.block(block);
	}

	public void fileDone() throws IOException {
		Break ad= factory.adbreak();
		sink.brk(ad);
	}

	public void close() throws IOException {
		sink.close();
	}

}
