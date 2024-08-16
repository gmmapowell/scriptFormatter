package com.gmmapowell.script.presenter.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.kNodes.KNodeItem;
import com.gmmapowell.script.processor.presenter.SlideFormatter;

public class Slide implements KNodeItem {
	private final String name;
	private String image;
	private Float xt;
	private Float yt;
	private String backgroundColor;
	private SlideFormatter sf;
	private List<SlideStep> steps = new ArrayList<>();

	public Slide(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void aspect(float xt, float yt) {
		this.xt = xt;
		this.yt = yt;
	}

	public float aspectx() {
		if (xt == null)
			return 16;
		return xt;
	}

	public float aspecty() {
		if (yt == null)
			return 9;
		return yt;
	}

	public void backgroundColor(String value) {
		this.backgroundColor = value;
	}

	public void backgroundImage(String value) {
		this.image = value;
	}
	
	@Override
	public String image() {
		return image;
	}

	@Override
	public String overlayImage() {
		if (sf == null)
			return null;
		return sf.overlayImage();
	}

	public void setFormat(SlideFormatter sf) {
		this.sf = sf;
	}

	public void speak(String value) {
		// TODO Auto-generated method stub
		
	}

	public void addStep(SlideStep step) {
		steps.add(step);		
	}

	@Override
	public void asJson(JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("name", name);
		if (xt != null) {
			gen.writeFieldName("aspect");
			gen.writeStartArray();
			gen.writeNumber(xt);
			gen.writeNumber(yt);
			gen.writeEndArray();
		}
		if (this.backgroundColor != null) {
			gen.writeStringField("background", this.backgroundColor);
		}
		gen.writeFieldName("steps");
		gen.writeStartArray();
		gen.writeStartObject();
		if (sf != null)
			sf.asJson(gen);
		gen.writeEndObject();
		for (SlideStep s : steps) {
			gen.writeStartObject();
			sf.stepJson(gen, s);
			gen.writeEndObject();
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}
	
	@Override
	public void meta(JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("name", name);
		if (xt != null) {
			gen.writeFieldName("aspect");
			gen.writeStartArray();
			gen.writeNumber(xt);
			gen.writeNumber(yt);
			gen.writeEndArray();
		}
		if (this.backgroundColor != null) {
			gen.writeStringField("background", this.backgroundColor);
		}
		gen.writeFieldName("steps");
		gen.writeStartArray();
		gen.writeStartObject();
		if (sf != null)
			sf.asJson(gen);
		gen.writeEndObject();
		for (SlideStep s : steps) {
			gen.writeStartObject();
			sf.stepJson(gen, s);
			gen.writeEndObject();
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}
}
