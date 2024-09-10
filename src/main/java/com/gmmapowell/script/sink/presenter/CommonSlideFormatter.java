package com.gmmapowell.script.sink.presenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zinutils.exceptions.CantHappenException;

import com.fasterxml.jackson.core.JsonGenerator;

public abstract class CommonSlideFormatter implements SlideFormatter {
	public class Field {
		public final String value;
		public final Map<String, String> options = new HashMap<>();

		public Field(String value) {
			this.value = value;
		}
	}

	protected final Slide slide;
	protected final Map<String, Field> fields = new HashMap<>();

	public CommonSlideFormatter(Slide slide) {
		this.slide = slide;
	}

	@Override
	public void field(String name, String value) {
		if (fields.containsKey(name))
			throw new CantHappenException("duplicate field " + name);
		fields.put(name, new Field(value));
	}

	@Override
	public void fieldOption(String field, String name, String value) {
		Field f = fields.get(field);
		f.options.put(name, value);
	}

	@Override
	public String overlayImage() {
		if (fields.containsKey("image"))
			return fields.get("image").value;
		else
			return null;
	}

	@Override
	public void asJson(JsonGenerator gen) throws IOException {
		float ax = slide.aspectx();
		float ay = slide.aspecty();
		gen.writeFieldName("slide");
		gen.writeStartArray();
		jsonFields(gen, ax, ay);
		gen.writeEndArray();
		gen.writeFieldName("speaker");
		gen.writeStartArray();
		if (fields.containsKey("title")) {
			gen.writeString(fields.get("title").value);
		}
		gen.writeEndArray();
	}
	
	@Override
	public void stepJson(JsonGenerator gen, SlideStep s) throws IOException {
		float ax = slide.aspectx();
		float ay = slide.aspecty();
		gen.writeFieldName("slide");
		gen.writeStartArray();
		s.jsonFields(gen, this, ax, ay);
		gen.writeEndArray();
		gen.writeFieldName("speaker");
		gen.writeStartArray();
		s.speakerNotes(gen);
		gen.writeEndArray();
	}

	@Override
	public void showSpeakerNotes(JsonGenerator gen, List<String> speaker, float ax, float ay) throws IOException {
		// generally ignored
	}
	
	protected abstract void jsonFields(JsonGenerator gen, float ax, float ay) throws IOException;

	protected void showText(JsonGenerator gen, Field field, float[] pin, float[] origin, float wid, float ht, AndMore r) throws IOException {
		if (field == null)
			return;
		gen.writeStartObject();
		gen.writeFieldName("showText");
		gen.writeStartObject();
		gen.writeStringField("text", field.value);
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
		for (Entry<String, String> e : field.options.entrySet()) {
			handleOption(gen, e.getKey(), e.getValue());
		}
		r.run();
		gen.writeEndObject();
		gen.writeEndObject();
	}

	private void handleOption(JsonGenerator gen, String key, String value) throws IOException {
		if (ignoreOption(key)) {
			return;
		}
		switch (key) {
		case "border":
		case "box":
			gen.writeStringField(key, value);
			break;
		default:
			System.out.println("cannot handle field option " + key);
			break;
		}
	}

	protected boolean ignoreOption(String key) {
		return false;
	}
}
