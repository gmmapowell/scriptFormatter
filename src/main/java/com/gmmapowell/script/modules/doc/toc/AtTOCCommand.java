package com.gmmapowell.script.modules.doc.toc;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.LinkFromTOC;
import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class AtTOCCommand implements AtCommandHandler {
	private final ConfiguredState state;
	private final TOCState toc;

	public AtTOCCommand(ScannerAtState sas) {
		this.state = sas.state();
		toc = sas.global().requireState(TOCState.class);
	}

	@Override
	public String name() {
		return "TOC";
	}

	@Override
	public void invoke(AtCommand cmd) {
		if (toc.currentMeta() == null)
			return;
		List<LinkFromTOC> links = new ArrayList<>();
		try {
			JSONArray order = toc.currentMeta().getJSONArray("toc");
			JSONObject headings = toc.currentMeta().getJSONObject("headings");
			for (int i=0;i<order.length();i++) {
				Object e = order.get(i);
				if (e instanceof String)
					e = headings.getJSONObject((String)e);
				JSONObject entry = (JSONObject) e;
//				String type = entry.getString("type");
				state.newPara("text"); // "tocline", "toc-" + type
				if (entry.has("number")) {
					state.newSpan(); // tocnumber
					state.text(entry.getString("number"));
					state.op(new BreakingSpace()); // NBSP?
				}
				state.newSpan(); // tocheading
				state.text(entry.getString("title"));
				state.newSpan(); // tocdots - how do we set the width of this?
				state.text("...");
				state.newSpan(); // tocpage - right justified
				String page = "??";
				if (entry.has("page")) {
					page = entry.getString("page");
				}
				LinkFromTOC lk = new LinkFromTOC(page, entry.getString("title"));
				links.add(lk);
				state.op(lk);
				state.endPara();
			}
			toc.toc().links(links);
		} catch (JSONException e) {
			throw WrappedException.wrap(e);
		}
	}

}
