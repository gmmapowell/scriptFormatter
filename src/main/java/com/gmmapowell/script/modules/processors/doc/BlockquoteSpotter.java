package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class BlockquoteSpotter implements ProcessingScanner {
	private final ConfiguredState state;
	private final BlockquoteState bs;

	public BlockquoteSpotter(ConfiguredState state) {
		this.state = state;
		bs = state.require(BlockquoteState.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (s.equals("$$")) {
			System.out.println("is-blockquote");
			bs.toggle();
			if (bs.active()) {
				state.pushFormat("blockquote");
			} else {
				state.popFormat("blockquote");
			}
			return true; // it has been handled
		} else if (bs.active()) {
			state.endPara(); // make sure the text will appear in a new para
//			state.newPara();
//			state.processText(s);
//			return true; // we processed the line
			return false; // the line still needs processing
		} else {
			return false;
		}
	}
}
