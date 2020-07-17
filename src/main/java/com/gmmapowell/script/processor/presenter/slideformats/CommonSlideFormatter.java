package com.gmmapowell.script.processor.presenter.slideformats;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.errors.ErrorReporter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.processor.presenter.SlideFormatter;

public abstract class CommonSlideFormatter implements SlideFormatter {
	protected final ErrorReporter errors;
	protected final Slide slide;
	protected final Map<String, String> fields = new HashMap<>();

	public CommonSlideFormatter(ErrorReporter errors, Slide slide) {
		this.errors = errors;
		this.slide = slide;
	}

	@Override
	public void field(InputPosition loc, String name, String value) {
		if (fields.containsKey(name))
			errors.message(loc, "duplicate field " + name);
		fields.put(name, value);
	}

	@Override
	public String overlayImage() {
		return fields.get("image");
	}

	@Override
	public void asJson(JsonGenerator gen) throws IOException {
		float ax = slide.aspectx();
		float ay = slide.aspecty();
		gen.writeFieldName("slide");
		gen.writeStartArray();
		jsonFields(gen, ax, ay);
		gen.writeEndArray();
	}

	protected abstract void jsonFields(JsonGenerator gen, float ax, float ay) throws IOException;

	protected void showText(JsonGenerator gen, String text, float[] pin, float[] origin, float wid, float ht, AndMore r) throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("showText");
		gen.writeStartObject();
		gen.writeStringField("text", text);
		gen.writeArrayFieldStart("pin");
		gen.writeNumber(pin[0]);
		gen.writeNumber(pin[1]);
		gen.writeEndArray();
		gen.writeArrayFieldStart("origin");
		gen.writeNumber(origin[0]);
		gen.writeNumber(origin[1]);
		gen.writeEndArray();
		gen.writeNumberField("width", wid);
		gen.writeNumberField("height", ht);
		r.run();
		gen.writeEndObject();
		gen.writeEndObject();
	}
}
