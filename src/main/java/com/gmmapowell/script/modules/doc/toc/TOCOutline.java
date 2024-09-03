package com.gmmapowell.script.modules.doc.toc;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.AnchorOp;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.prose.TOCEntry;

public class TOCOutline implements DocumentOutline {
	private final ConfiguredState sink;
	private final TOCState state;

	public TOCOutline(ScannerAtState sas) {
		this.sink = sas.state();
		state = sas.state().require(TOCState.class);
		state.configureOnCreate();
	}

	@Override
	public void entry(int level, String title, String style, String anchor) {
		switch (level) {
		case 1: { /* chapter */
			if (!style.equals(state.chapterStyle))
				state.resetNumbering();
			state.chapterStyle = style;
			state.reset();
			TOCEntry entry = null;
			String tx = null;
			if (state.chapterStyle.equals("chapter")) {
				String number = Integer.toString(state.chapter);
				entry = state.toc().chapter(anchor, number, title);
				tx = number + " ";
				state.wantSectionNumbering = true;
				state.chapter++;
				state.section = 1;
			} else if (state.chapterStyle.equals("appendix")) {
				String number = new String(new char[] { (char) ('@' + state.chapter) });
				entry = state.toc().chapter(anchor, number, title);
				tx = number + " ";
				state.wantSectionNumbering = true;
				state.chapter++;
				state.section = 1;
			} else {
				entry = state.toc().chapter(anchor, null, title);
				state.wantSectionNumbering = false;
			}
			if (entry != null) {
				sink.newSpan();
				sink.op(new AnchorOp(entry));
				if (tx != null)
					sink.processText(tx);
			}
			break;
		}
		case 2: {
			TOCEntry entry;
			String tx = null;
			if (state.chapterStyle.equals("chapter")) {
				String number = Integer.toString(state.chapter-1) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
				entry = state.toc().section(anchor, number, title);
				tx = number + " ";
			} else if (state.chapterStyle.equals("appendix")) {
				String number = new String(new char[] { (char) ('@' + state.chapter-1) }) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
				entry = state.toc().section(anchor, number, title);
				tx = number + " ";
			} else {
				entry = state.toc().section(anchor, null, title);
			}
			if (entry != null) {
				sink.newSpan();
				sink.op(new AnchorOp(entry));
				if (tx != null)
					sink.processText(tx);
			}
			
			state.section++;
			break;
		}
		default:
			throw new NotImplementedException();
		}
	}
}
