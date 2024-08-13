package com.gmmapowell.script.sink.capture;

import java.io.IOException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;

public class CaptureSinkInFile implements Sink {
	private final Region region;
	private final Sink sink;

	public CaptureSinkInFile(Region region, Sink sink) {
		this.region = region;
		this.sink = sink;
	}

	public void flow(Flow flow) {
		try {
			new FlowDumper(region.ensurePlace("fred.flow")).dump(flow);
		} catch (IOException ex) {
			System.out.println("could not capture flow " + flow.name + ": " + ex);
		}
		sink.flow(flow);
	}

	public void render() throws IOException {
		sink.render();
	}

	public void showFinal() {
		sink.showFinal();
	}

	public void upload() throws Exception {
		sink.upload();
	}

}
