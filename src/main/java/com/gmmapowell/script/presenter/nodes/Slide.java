package com.gmmapowell.script.presenter.nodes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.kNodes.KNodeItem;

public class Slide implements KNodeItem {
	private final String name;
	private String image;
	private Float xt;
	private Float yt;

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

	public void background(String value) {
		this.image = value;
	}
	
	@Override
	public String image() {
		return image;
	}

	public void setFormat(String name) {
		// TODO Auto-generated method stub
		
	}

	public void setTitle(String value) {
		// TODO Auto-generated method stub
		
	}

	public void speak(String value) {
		// TODO Auto-generated method stub
		
	}

	public void addStep(SlideStep step) {
		// TODO Auto-generated method stub
		
	}

	public void field(String field, String value) {
		// TODO Auto-generated method stub
		
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
		gen.writeEndObject();
	}
}
