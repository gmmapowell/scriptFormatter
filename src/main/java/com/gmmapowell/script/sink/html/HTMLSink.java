package com.gmmapowell.script.sink.html;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.StyledToken;
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
	public void prepare() throws Exception {
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
	}

	@Override
	public void render() throws IOException {
		System.out.println("render() from HTMLSink");
		for (Flow f : flows) {
			HTMLTokenProcessor proc = new HTMLTokenProcessor(storeInto, pre, post, ipg);
			for (Section s : f.sections) {
				proc.beginSection(null);
				Cursor c = new Cursor(f.name, s);
				StyledToken tok;
				while ((tok = c.next()) != null) {
					proc.processToken(null, tok);
				}
				proc.endSection();
			}
//			writer.close();
//			if (saveAs != null) {
//				Place html = storeInto.ensureRegionAndPlace(saveAs + ".html");
//				html.store(sw.toString());
////				FileUtils.cat(GeoFSUtils.file(html));
//			}
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
