package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class BulletSpotter implements ProcessingScanner {
	private final ConfiguredState state;

	public BulletSpotter(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("*")) {
			System.out.println("is-bullet");
			int idx = s.indexOf(" ");
			if (idx == 1)
				state.newPara("bullet");
			else
				state.newPara("bullet" + idx);
			state.newSpan("bullet-sign");
			state.text("\u2022");
			state.endSpan();
			state.processText(s.substring(idx+1).trim());
			state.observeBlanks();
			return true;
		}
			
		return false;
	}

}
