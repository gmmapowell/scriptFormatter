package com.gmmapowell.script.presenter.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.processor.presenter.SlideFormatter;

public class SlideStep {
	private final List<String> speaker = new ArrayList<>();

	public void speak(String message) {
		speaker.add(message);
	}

	public void img(String file) {
		// TODO Auto-generated method stub
		
	}

	public void jsonFields(JsonGenerator gen, SlideFormatter slideFormatter, float ax, float ay) throws IOException {
		slideFormatter.showSpeakerNotes(gen, speaker, ax, ay);
	}

	public void speakerNotes(JsonGenerator gen) throws IOException {
		for (String s : speaker)
			gen.writeString(s);
	}
}
