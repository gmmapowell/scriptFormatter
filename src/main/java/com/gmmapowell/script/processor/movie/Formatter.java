package com.gmmapowell.script.processor.movie;

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

	public void title(String title) {
		if (debug)
			System.out.println("# " + title);
		sink.block(factory.block("title", title.toUpperCase()));
	}

	public void slug(String slug) {
		if (debug)
			System.out.println("! " + slug);
	}

	public void speech(String text) {
		if (debug)
			System.out.println("<< " + text);
	}

	public void scene(String text) {
		if (debug)
			System.out.println("... " + text);
	}

}
