package com.gmmapowell.script.elements.block;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.ElementFactory;

public class BlockishElementFactory implements ElementFactory {

	@Override
	public Block block(String format, String text) {
		return new TextBlock(format, text);
	}

}
