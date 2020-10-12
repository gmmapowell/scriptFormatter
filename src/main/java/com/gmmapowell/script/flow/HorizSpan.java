package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class HorizSpan {
	public final HorizSpan parent;
	public final List<String> formats;
	public final List<SpanItem> items = new ArrayList<>();

	public HorizSpan(HorizSpan parent, List<String> formats) {
		this.parent = parent;
		this.formats = new ArrayList<>(formats);
	}
}
