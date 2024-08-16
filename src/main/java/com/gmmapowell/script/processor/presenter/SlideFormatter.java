package com.gmmapowell.script.processor.presenter;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.gmmapowell.script.presenter.nodes.SlideStep;

public interface SlideFormatter {
	public final float[] BASELEFT = new float[] { -1f, 0 };
	public final float[] BASECENTER = new float[] { 0, 0 };
	public final float[] BASERIGHT = new float[] { 1f, 0 };

	void field(String name, String value);
	void fieldOption(String field, String name, String value);
	
	String overlayImage();

	void asJson(JsonGenerator gen) throws IOException;
	void stepJson(JsonGenerator gen, SlideStep s) throws IOException;
	void showSpeakerNotes(JsonGenerator gen, List<String> speaker, float ax, float ay) throws IOException;
}
