package com.gmmapowell.script.sink.presenter;

import java.util.List;

import com.gmmapowell.script.flow.SpanItem;

public class ZipItem {
	public final Iterable<SpanItem> present;
	public final Iterable<SpanItem> notes;
	
	public ZipItem(List<SpanItem> p, List<SpanItem> n) {
		this.present = p;
		this.notes = n;
	}
}
