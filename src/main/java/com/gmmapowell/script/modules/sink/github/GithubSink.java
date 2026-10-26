package com.gmmapowell.script.modules.sink.github;

import java.io.IOException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;

public class GithubSink implements Sink {
	private final Region region;
	private final Region r;
	private final String pdf;
	private final String epub;

	public GithubSink(Region region, Region r, String pdf, String epub) {
		this.region = region;
		this.r = r;
		this.pdf = pdf;
		this.epub = epub;
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public void flow(Flow flow) {
	}

	@Override
	public void render() throws IOException {
	}

	@Override
	public void showFinal() {
	}

	
	@Override
	public void flush() throws IOException {
		if (pdf != null) {
			Place topdf = region.ensurePlace(pdf);
			r.place(pdf).copyBinary(topdf);
		}
		if (epub != null) {
			Place toepub = region.ensurePlace(epub);
			r.place(epub).copyBinary(toepub);
		}
	}

	@Override
	public void upload() throws Exception {
	}

	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

}
