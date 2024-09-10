package com.gmmapowell.script.modules.processors.movie;

import java.io.IOException;

import com.gmmapowell.script.modules.movie.BoxyAdBreak;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class Formatter {
	private final ConfiguredState state;
	private final boolean debug;

	public Formatter(ConfiguredState state, boolean debug) {
		this.state = state;
		this.debug = debug;
	}

	public void title(String title) throws IOException {
		if (debug)
			System.out.println("# " + title);
		state.newPara("title");
		state.newSpan();
		state.text(title.toUpperCase());
	}

	public void slug(String slug) throws IOException {
		if (debug)
			System.out.println("! " + slug);
		state.newPara("slug");
		state.newSpan();
		state.text(slug.toUpperCase());
	}

	public void speaker(String speaker) throws IOException {
		if (debug)
			System.out.println("| " + speaker);
		state.newPara("speaker");
		state.newSpan();
		state.text(speaker);
	}

	public void direction(String text) throws IOException {
		if (debug)
			System.out.println("| " + text);
		state.newPara("direction");
		state.processText(text);
	}

	public void speech(String speech) throws IOException {
		if (debug)
			System.out.println("<< " + speech);
		state.newPara("speech");
		state.processText(speech);
	}

	public void endSpeech() throws IOException {
	}

	public void scene(String text) throws IOException {
		if (debug)
			System.out.println("... " + text);
		state.newPara("scene");
		state.processText(text);
	}

	public void fileDone() throws IOException {
		state.newPara("scene");
		state.newSpan();
		state.op(new BoxyAdBreak());
	}
}
