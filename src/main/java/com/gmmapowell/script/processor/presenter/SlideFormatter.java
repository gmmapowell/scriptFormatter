package com.gmmapowell.script.processor.presenter;

import java.io.IOException;

import org.flasck.flas.blockForm.InputPosition;

import com.fasterxml.jackson.core.JsonGenerator;

public interface SlideFormatter {
	public final float[] BASECENTER = new float[] { 0, 0 };
	public final float[] BASERIGHT = new float[] { 1f, 0 };

	void field(InputPosition loc, String name, String value);
	String overlayImage();

	void asJson(JsonGenerator gen) throws IOException;
}
