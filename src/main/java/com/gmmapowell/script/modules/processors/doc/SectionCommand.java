package com.gmmapowell.script.modules.processors.doc;

public class SectionCommand implements AtCommandHandler {

	public SectionCommand(ScannerAtState state) {
		
	}
	
	@Override
	public String name() {
		return "Section";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.args.get("title");
		if (title == null)
			throw new RuntimeException("Section without title");
		String anchor = cmd.args.get("anchor");
		/*
		TOCEntry entry;
		if (state.chapterStyle.equals("chapter")) {
			String number = Integer.toString(state.chapter-1) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
			entry = toc.section(anchor, number, title);
			title = number + " " + title;
		} else if (state.chapterStyle.equals("appendix")) {
			String number = new String(new char[] { (char) ('@' + state.chapter-1) }) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
			entry = toc.section(anchor, number, title);
			title = number + " " + title;
		} else {
			entry = toc.section(anchor, null, title);
		}
		state.newPara("section-title");
		if (entry != null) {
			state.newSpan();
			state.op(new AnchorOp(entry));
		}
		ProcessingUtils.process(state, title);
		state.endPara();
		
		state.section++;
		*/
	}
}
