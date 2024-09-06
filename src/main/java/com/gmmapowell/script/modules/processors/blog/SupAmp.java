package com.gmmapowell.script.modules.processors.blog;

import com.gmmapowell.script.modules.processors.doc.InlineDocCommandState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;

public class SupAmp implements InlineCommandHandler {
	private final ConfiguredState state;

	public SupAmp(InlineDocCommandState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "sup";
	}

	@Override
	public void invoke() {
		if (!state.inPara())
			state.newPara();
		if (!state.inSpan())
			state.newSpan();
		if (state.topSpanHas("superscript")) {
			state.popSpan();
		} else {
			state.nestSpan("superscript");
		}
	}

}
