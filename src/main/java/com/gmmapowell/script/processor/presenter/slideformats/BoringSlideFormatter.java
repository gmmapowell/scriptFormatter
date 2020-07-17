package com.gmmapowell.script.processor.presenter.slideformats;

import java.io.IOException;

import org.flasck.flas.errors.ErrorReporter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.presenter.nodes.Slide;

public class BoringSlideFormatter extends CommonSlideFormatter {

	public BoringSlideFormatter(ErrorReporter errors, Slide slide) {
		super(errors, slide);
	}

	@Override
	protected void jsonFields(JsonGenerator gen, float ax, float ay) throws IOException {
		if (fields.containsKey("title")) {
			showText(gen, fields.get("title"), BASECENTER, new float[] { 0, ay/2 - .5f }, ax*0.9f, ay/10, () -> {
			});
		}
	}

}
