package com.gmmapowell.script.processor.presenter.slideformats;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.presenter.nodes.Slide;

public class BoringSlideFormatter extends CommonSlideFormatter {
	private int y = 0;
	
	public BoringSlideFormatter(Slide slide) {
		super(slide);
	}

	@Override
	protected void jsonFields(JsonGenerator gen, float ax, float ay) throws IOException {
		if (fields.containsKey("title")) {
			showText(gen, fields.get("title"), BASECENTER, new float[] { 0, ay/2 - .5f }, ax*0.9f, ay/10, () -> {
			});
		}
	}

	@Override
	public void showSpeakerNotes(JsonGenerator gen, List<String> notes, float ax, float ay) throws IOException {
		for (String s : notes) {
			showText(gen, new Field(s), BASELEFT, new float[] { -ax*0.45f, ay * .3f - y * ay/12 }, ax*0.9f, ay/15, () -> {
				gen.writeStringField("box", "#7f7f7fc0");
			});
			y++;
		}
	}
}
