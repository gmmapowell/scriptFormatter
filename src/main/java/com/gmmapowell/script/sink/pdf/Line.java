package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class Line {
	public final List<Segment> segments;

	public Line(List<Segment> segments) {
		this.segments = new ArrayList<>(segments);
	}

	public void render(PDPageContentStream page, float x, float y) throws IOException {
		for (Segment s : segments) {
			s.render(page, x, y);
			x += s.width();
		}
	}

	public float getLineWidth() throws IOException {
		float ret = 0f;
		for (Segment s : segments)
			ret += s.width();
		return ret;
	}

	public float height() throws IOException {
		float ret = 0f;
		for (Segment s : segments)
			ret = Math.max(s.height(), ret);
		return ret;
	}

	public static boolean canHandle(float wid, List<Segment> segments) throws IOException {
		float c = 0f;
		for (Segment s : segments)
			c += s.width();
		return c <= wid;
	}

	public float getBaseline() {
		float ret = 0f;
		for (Segment s : segments)
			ret = Math.max(s.baseline(), ret);
		return ret;
	}
}
