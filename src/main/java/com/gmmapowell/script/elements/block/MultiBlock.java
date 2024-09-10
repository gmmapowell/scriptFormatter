package com.gmmapowell.script.elements.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Group;

public class MultiBlock implements Group {
	private final List<Block> blocks = new ArrayList<>();

	public MultiBlock() {
	}

	@Override
	public void addBlock(Block block) {
		blocks.add(block);
	}

	@Override
	public Iterator<Block> iterator() {
		return blocks.iterator();
	}
}
