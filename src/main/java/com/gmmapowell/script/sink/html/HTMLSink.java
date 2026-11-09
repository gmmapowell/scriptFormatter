package com.gmmapowell.script.sink.html;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowCursor;
import com.gmmapowell.script.modules.sink.github.IndexPageCreator;
import com.gmmapowell.script.sink.Sink;

public class HTMLSink implements Sink {
	public enum Mode {
		START, NORMAL, LIST, BLOCKQUOTE
	}

	private List<Flow> flows = new ArrayList<>();
	private final Region storeInto;
	private Place post, pre;
	private IndexPageCreator ipg;
	private boolean separately;

	public HTMLSink(Region root, String storeInto) throws IOException, GeneralSecurityException {
		this.storeInto = root.ensureSubregion(storeInto);
	}

	public HTMLSink(Region root, Region storeAs, Place prep, Place postp, IndexPageCreator ipg) {
		this.storeInto = storeAs;
		this.pre = prep;
		this.post = postp;
		this.ipg = ipg;
	}

	@Override
	public void prepare(boolean separately) throws Exception {
		this.separately = separately;
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
	}

	@Override
	public void render() throws IOException {
		FlowCursor fc = new FlowCursor(flows, separately);
		HTMLTokenProcessor proc = new HTMLTokenProcessor(storeInto, pre, post, ipg);
		fc.run(proc);
		if (ipg != null) {
			ipg.endUL();
		}
	}

	@Override
	public void showFinal() {
	}

	@Override
	public void upload() throws Exception {
	}

	@Override
	public void finish() throws Exception {
	}
}
