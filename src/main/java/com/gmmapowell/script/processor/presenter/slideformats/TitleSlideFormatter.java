package com.gmmapowell.script.processor.presenter.slideformats;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.presenter.nodes.Slide;

public class TitleSlideFormatter extends CommonSlideFormatter {
	public TitleSlideFormatter(Slide slide) {
		super(slide);
	}

	protected void jsonFields(JsonGenerator gen, float ax, float ay) throws IOException {
		if (fields.containsKey("title")) {
			showText(gen, fields.get("title"), BASECENTER, new float[] { 0, ay/10f }, ax*0.9f, ay/6, () -> {
			});
		}
		if (fields.containsKey("subtitle")) {
			showText(gen, fields.get("subtitle"), BASECENTER, new float[] { 0, ay/20f }, ax*0.75f, ay/12, () -> {
				gen.writeStringField("style", "italic");
			});
		}
		if (fields.containsKey("name")) {
			showText(gen, fields.get("name"), BASERIGHT, new float[] { ax/3f, -ay/5 }, ax*0.45f, ay/30, () -> {
			});
		}
		if (fields.containsKey("company")) {
			showText(gen, fields.get("company"), BASERIGHT, new float[] { ax/3f, -ay/4.3f }, ax*0.45f, ay/36, () -> {
				gen.writeStringField("style", "italic");
			});
		}
	}
}
