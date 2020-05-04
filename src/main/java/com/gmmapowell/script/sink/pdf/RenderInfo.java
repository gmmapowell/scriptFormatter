package com.gmmapowell.script.sink.pdf;

import java.util.List;

public class RenderInfo {
	final float beforeBlock;
	final List<Line> lines;

	public RenderInfo(float afterBlock, List<Line> lines) {
		this.beforeBlock = afterBlock;
		this.lines = lines;
	}

}
