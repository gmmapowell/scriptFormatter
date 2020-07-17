package com.gmmapowell.script.kNodes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface KNodeItem {
	String name();
	String image();
	String overlayImage();
	void asJson(JsonGenerator gen) throws IOException;
}
