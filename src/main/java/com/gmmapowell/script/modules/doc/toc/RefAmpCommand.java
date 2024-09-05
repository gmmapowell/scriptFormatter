package com.gmmapowell.script.modules.doc.toc;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.InvalidUsageException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.flow.LinkFromRef;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class RefAmpCommand implements AmpCommandHandler {
	private final ConfiguredState state;
	private final TOCState toc;

	public RefAmpCommand(ScannerAmpState sas) {
		this.state = sas.state();
		toc = sas.global().requireState(TOCState.class);
	}
	
	@Override
	public String name() {
		return "ref";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		// TODO: formatting should be customizable
		if (!cmd.args.hasMore())
			throw new RuntimeException("&ref command needs a reference");
		String anchor = cmd.args.readString();
		if (!state.inSpan())
			state.newSpan();
		String tx = "unref";
		JSONObject meta = toc.currentMeta();
		if (meta != null) {
			try {
				JSONObject anchors = meta.getJSONObject("anchors");
				if (anchors.has(anchor)) {
					JSONObject anch = anchors.getJSONObject(anchor);
					if (!anch.has("number"))
						throw new InvalidUsageException("the anchor '" + anchor + "' does not have a section number");
					tx = anch.getString("number");
				} else {
					System.out.println("there is no anchor '" + anchor + "'");
				}
			} catch (JSONException e) {
				throw WrappedException.wrap(e);
			}
		}
		state.op(new LinkFromRef(toc.toc(), anchor, '§' + tx));
	}
}
