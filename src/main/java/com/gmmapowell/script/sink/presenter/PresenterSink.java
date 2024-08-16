package com.gmmapowell.script.sink.presenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.kNodes.Galaxy;
import com.gmmapowell.script.modules.presenter.AspectOp;
import com.gmmapowell.script.modules.presenter.BgColorOp;
import com.gmmapowell.script.modules.presenter.BgImageOp;
import com.gmmapowell.script.modules.presenter.FieldOp;
import com.gmmapowell.script.modules.presenter.FieldOptionOp;
import com.gmmapowell.script.modules.presenter.FormatOp;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.processor.presenter.SlideFormatter;
import com.gmmapowell.script.processor.presenter.slideformats.BoringSlideFormatter;
import com.gmmapowell.script.processor.presenter.slideformats.TitleSlideFormatter;
import com.gmmapowell.script.sink.Sink;

public class PresenterSink implements Sink {
	private Region root;
	private String output;
	private String meta;
	private final List<Flow> flows = new ArrayList<>();
	
	public PresenterSink(Region root, String output, String meta, boolean wantOpen, String upload, boolean debug) throws IOException {
		this.root = root;
		this.output = output;
		this.meta = meta;
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
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
		List<Slide> slides = new ArrayList<>();
		for (Flow f : flows) {
			slides.add(renderSlide(f));
		}
		Galaxy<Slide> g = new Galaxy<Slide>(metaJson, slides );
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
	
	public Slide renderSlide(Flow f) {
		Slide ret = new Slide(f.name);
		
		for (Section s : f.sections) {
			if ("meta".equals(s.format))
				doMetas(ret, s);
			else
				doMetas(ret, s); // this kind of feels wrong, but it seems to work for now...
		}
		return ret;
	}
	
	private void doMetas(Slide slide, Section s) {
		SlideFormatter ret = slide.formatter();
		for (Para p : s.paras) {
			for (HorizSpan span : p.spans) {
				for (SpanItem item : span.items) {
//					System.out.println(" have " + item);
					if (item instanceof FormatOp) {
						ret = findSlideFormatter(slide, ((FormatOp)item).format);
					} else if (item instanceof AspectOp) {
						AspectOp as = (AspectOp)item;
						slide.aspect(as.x, as.y);
					} else if (item instanceof BgImageOp) {
						slide.backgroundImage(((BgImageOp)item).url);
					} else if (item instanceof BgColorOp) {
						slide.backgroundColor(((BgColorOp)item).color);
					} else if (item instanceof FieldOp) {
						FieldOp f = (FieldOp) item;
						ret.field(f.name, f.sval);
					} else if (item instanceof FieldOptionOp) {
						FieldOptionOp f = (FieldOptionOp) item;
						ret.fieldOption(f.field, f.name, f.sval);
					} else if (item instanceof TextSpanItem) {
						ret.field("title", ((TextSpanItem) item).text);
					} else
						System.out.println("huh? " + item);
				}
			}
		}
		if (ret == null)
			throw new CantHappenException("there was no slide format for " + slide.name());

		slide.setFormat(ret);
	}

	private SlideFormatter findSlideFormatter(Slide slide, String format) {
		switch (format) {
		case "title-slide":
			return new TitleSlideFormatter(slide);
		case "boring-slide":
			return new BoringSlideFormatter(slide);
		default:
			throw new CantHappenException("there is no formatter for slide " + format);
		}
	}
}
