package com.gmmapowell.script.processor.presenter;

import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;

public interface SlideCollector {

	void metaOp(SpanItem op);

	void text(Section s, String tx);

	HorizSpan span(Section s);

}