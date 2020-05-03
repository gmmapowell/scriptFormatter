package com.gmmapowell.script.processor.movie;

import java.io.IOException;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.sink.Sink;

public class Formatter {
	private final ElementFactory factory;
	private final Sink sink;
	private final boolean debug;

	public Formatter(ElementFactory factory, Sink outputTo, boolean debug) {
		this.factory = factory;
		this.sink = outputTo;
		this.debug = debug;
	}

	public void title(String title) throws IOException {
		if (debug)
			System.out.println("# " + title);
		sink.block(factory.block("title", title.toUpperCase()));
	}

	public void slug(String slug) throws IOException {
		if (debug)
			System.out.println("! " + slug);
		sink.block(factory.block("slug", slug));
	}

	public void speech(String speech) throws IOException {
		if (debug)
			System.out.println("<< " + speech);
		sink.block(factory.block("speech", speech));
	}

	public void scene(String text) throws IOException {
		if (debug)
			System.out.println("... " + text);
		sink.block(factory.block("scene", text));
	}

	public void close() throws IOException {
		sink.close();
	}

}
