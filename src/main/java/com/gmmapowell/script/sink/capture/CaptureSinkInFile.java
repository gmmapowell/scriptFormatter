package com.gmmapowell.script.sink.capture;

import java.io.IOException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;

public class CaptureSinkInFile implements Sink {
	private final Sink sink;
	private final FlowDumper dumper;

	public CaptureSinkInFile(Region region, Sink sink) throws IOException {
		this.sink = sink;
		dumper = new FlowDumper(region.ensurePlace("fred.flow"));
	}

	@Override
	public void prepare() throws Exception {
		sink.prepare();
	}

	public void flow(Flow flow) {
		try {
			dumper.dump(flow);
		} catch (IOException ex) {
			System.out.println("could not capture flow " + flow.name + ": " + ex);
		}
		sink.flow(flow);
	}

	public void render() throws IOException {
		dumper.close();
		sink.render();
	}

	public void showFinal() {
		sink.showFinal();
	}

	public void upload() throws Exception {
		sink.upload();
	}

}
