package com.gmmapowell.script.presenter.nodes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.kNodes.KNodeItem;

public class Slide implements KNodeItem {
	private final String name;

	public Slide(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}
	
	@Override
	public String image() {
		return "Alan-turing-statue.png";
	}

	public void setFormat(String name) {
		// TODO Auto-generated method stub
		
	}

	public void setTitle(String value) {
		// TODO Auto-generated method stub
		
	}

	public void soeak(String value) {
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
		gen.writeFieldName("name");
		gen.writeString(name);
		gen.writeEndObject();
	}
}
