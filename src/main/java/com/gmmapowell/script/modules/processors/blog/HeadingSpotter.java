package com.gmmapowell.script.modules.processors.blog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmmapowell.script.modules.processors.doc.BlockquoteState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class HeadingSpotter implements ProcessingScanner{
	private final Pattern head = Pattern.compile("^(\\++) (.*)");
	private final ConfiguredState state;
	private final BlockquoteState bs;
	
	public HeadingSpotter(ConfiguredState state) {
		this.state = state;
		bs = state.require(BlockquoteState.class);
	}
	
	@Override
	public boolean handleLine(String s) {
		if (bs.active())
			return false;
		
		Matcher m = head.matcher(s);
		if (m.matches()) {
			int level = m.group(1).length();
			System.out.println("heading " + level);
			state.newPara("h" + level);
			state.processText(m.group(2));
			state.endPara();
			state.ignoreNextBlanks();
			return true;
		}
		return false;
	}

}
