package com.gmmapowell.script.modules.doc.toc;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.modules.processors.doc.OutlineNumbering;

public class DefaultNumbering implements OutlineNumbering {
//	private final ConfiguredState sink;
	private final TOCState state;

	public DefaultNumbering(TOCState state) {
		this.state = state;
//		this.sink = sas.state();
//		state = sas.global().requireState(TOCState.class);
	}

	@Override
	public void parseFormats(String tocFormat) {
	}

	@Override
	public String format(int level, String title, String style, String anchor) {
		String tx = null;

		switch (level) {
		case 1: { /* chapter */
			if (!style.equals(state.chapterStyle))
				state.resetNumbering();
			state.chapterStyle = style;
			state.reset();
			if (state.chapterStyle.equals("chapter")) {
				String number = Integer.toString(state.chapter);
				tx = number + " ";
				state.wantSectionNumbering = true;
				state.chapter++;
				state.section = 1;
			} else if (state.chapterStyle.equals("appendix")) {
				String number = new String(new char[] { (char) ('@' + state.chapter) });
				tx = number + " ";
				state.wantSectionNumbering = true;
				state.chapter++;
				state.section = 1;
			} else {
				state.wantSectionNumbering = false;
			}
			break;
		}
		case 2: {
			if (state.chapterStyle.equals("chapter")) {
				String number = Integer.toString(state.chapter-1) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
				tx = number + " ";
			} else if (state.chapterStyle.equals("appendix")) {
				String number = new String(new char[] { (char) ('@' + state.chapter-1) }) + "." + Integer.toString(state.section) + (state.commentary?"c":"");
				tx = number + " ";
			} else {
			}
			
			state.section++;
			break;
		}
		case 3: {
			state.section = 1;
			state.commentary = true;
			break;
		}
		case 4: {
//			TOCEntry entry = state.toc().subsubsection(anchor, null, title);
//			if (entry != null) {
//				sink.newSpan();
//				sink.op(new AnchorOp(entry));
//			}
			break;
		}
		case 5: {
//			TOCEntry entry = state.toc().subsubsection(anchor, null, title);
//			if (entry != null) {
//				sink.newSpan();
//				sink.op(new AnchorOp(entry));
//			}
			break;
		}
		default:
			throw new NotImplementedException();
		}
		
		return tx;
	}
}
