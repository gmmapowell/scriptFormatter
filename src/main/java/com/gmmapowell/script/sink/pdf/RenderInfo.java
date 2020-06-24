package com.gmmapowell.script.sink.pdf;

import java.util.List;

public class RenderInfo {
	final boolean beginNewPage;
	final float beforeBlock;
	final List<Line> lines;
	final boolean showAtBottom;

	public RenderInfo(boolean beginNewPage, boolean showAtBottom, float afterBlock, List<Line> lines) {
		this.beginNewPage = beginNewPage;
		this.showAtBottom = showAtBottom;
		this.beforeBlock = afterBlock;
		this.lines = lines;
	}

}
