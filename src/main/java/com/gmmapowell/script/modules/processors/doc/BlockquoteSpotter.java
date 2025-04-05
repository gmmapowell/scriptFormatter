package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.NonBreakingSpace;
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
	public boolean wantTrimmed() {
		return false;
	}

	@Override
	public boolean handleLine(String s) {
		if (s.trim().equals("$$")) {
//			System.out.println("is-blockquote");
			bs.toggle();
			if (bs.active()) {
				state.pushFormat("blockquote");
			} else {
				state.popFormat("blockquote");
			}
			return true; // it has been handled
		} else if (bs.active()) {
//			System.out.println("in-bq");
//			state.endPara(); // make sure the text will appear in a new para
			state.newPara();
//			state.newPara("blockquote");
			state.newSpan();
			int i=0;
			for (i=0;i<s.length();i++) {
				if (s.charAt(i) == '|') {
					state.op(new NonBreakingSpace());
					state.op(new NonBreakingSpace());
				} else if (s.charAt(i) == ' ') {
					state.op(new NonBreakingSpace());
				} else
					break;
			}
			while (i < s.length() && Character.isWhitespace(s.charAt(i)))
				i++;
			state.processText(s.substring(i));
			state.endPara();
			return true; // we processed the line
//			return false; // the line still needs processing
		} else {
			return false;
		}
	}
}
