package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.AnchorOp;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.TOCEntry;

public class ChapterCommand implements AtCommandHandler {

	public ChapterCommand(ScannerAtState state) {
		
	}
	
	@Override
	public String name() {
		return "Chapter";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.args.get("title");
		if (title == null)
			throw new RuntimeException("Chapter without title");
		String style = cmd.args.get("style");
		if (style == null)
			style = "chapter";
		/*
		if (!style.equals(state.chapterStyle))
			state.resetNumbering();
		state.chapterStyle = style;
		String anchor = cmd.args.get("anchor");
		state.reset();
		TOCEntry entry;
		if (state.chapterStyle.equals("chapter")) {
			String number = Integer.toString(state.chapter);
			entry = toc.chapter(anchor, number, title);
			title = number + " " + title;
			state.wantSectionNumbering = true;
			state.chapter++;
			state.section = 1;
		} else if (state.chapterStyle.equals("appendix")) {
			String number = new String(new char[] { (char) ('@' + state.chapter) });
			entry = toc.chapter(anchor, number, title);
			title = number + " " + title;
			state.wantSectionNumbering = true;
			state.chapter++;
			state.section = 1;
		} else {
			entry = toc.chapter(anchor, null, title);
			state.wantSectionNumbering = false;
		}
		state.newSection("footnotes", style);
		state.newSection("main", style);
		state.newPara("chapter-title");
		if (entry != null) {
			state.newSpan();
			state.op(new AnchorOp(entry));
		}
		ProcessingUtils.process(state, title);
		state.endPara();
		*/
	}

}
