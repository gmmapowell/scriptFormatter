package com.gmmapowell.script.sink.presenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.errors.ErrorResult;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.kNodes.Galaxy;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.processor.presenter.SlideFormatter;
import com.gmmapowell.script.processor.presenter.slideformats.BoringSlideFormatter;
import com.gmmapowell.script.processor.presenter.slideformats.TitleSlideFormatter;
import com.gmmapowell.script.sink.Sink;

public class PresenterSink implements Sink {
	private Region root;
	private String output;
	private String meta;
	private final List<Slide> slides = new ArrayList<>();
	
	// TODO: this should go away
	ErrorResult errors = null;

	public PresenterSink(Region root, String output, String meta, boolean wantOpen, String upload, boolean debug) throws IOException {
		this.root = root;
		this.output = output;
		this.meta = meta;
	}

	@Override
	public void flow(Flow flow) {
		Slide s = new Slide(flow.name);
		slides.add(s);
	}
	
	@Override
	public void render() {
		Place m = root.ensurePlace(meta);
		JSONObject metaJson = null;
		if (m.exists()) {
			try {
				metaJson = GeoFSUtils.readJSON(m);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Galaxy<Slide> g = new Galaxy<Slide>(metaJson, slides);
		Place f = root.ensurePlace(output);
		try {
			g.asJson(f.writer());
			g.writeMeta(m.writer());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	@Override
	public void showFinal() {
	}

	@Override
	public void upload() throws Exception {
	}
	

	private SlideFormatter findSlideFormatter(InputPosition loc, Slide slide, String format) {
		switch (format) {
		case "title-slide":
			return new TitleSlideFormatter(errors, slide);
		case "boring-slide":
			return new BoringSlideFormatter(errors, slide);
		default:
			errors.message(loc, "there is no formatter for slide " + format);
			return null;
		}
	}
}
