package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class DocPipeline extends ProsePipeline<DocState> {
	private DocState root = null;
	
	public DocPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}
	
	@Override
	protected DocState begin() {
		if (root == null) {
			root = new DocState();
		}
		root.reset();
		return root;
	}

	@Override
	protected void handleLine(DocState state, String s) throws IOException {
//		if (s.equals("$$")) {
//			blockquote = !blockquote;
//			continue;
//		} else if (s.startsWith("+")) {
//			if (curr != null) {
//				sink.block(curr);
//			}
//			curr = ef.block(headingLevel(s));
//			ProcessingUtils.addSpans(ef, curr, s.substring(s.indexOf(" ")+1).trim());
//			continue;
//		} else 
		if (s.startsWith("@")) {
			// it's a block starting command
			if (state.curr != null) {
				sink.block(state.curr);
				state.curr = null;
			}
			state.cmd = new DocCommand(s.substring(1));
		} else if (state.cmd != null) {
			int pos = s.indexOf('=');
			if (pos == -1)
				throw new RuntimeException("invalid argument to " + state.cmd + ": " + s);
			state.cmd.arg(s.substring(0, pos).trim(), s.substring(pos+1).trim());
		} else if (s.startsWith("&")) {
			int idx = s.indexOf(" ");
			String cmd;
			if (idx == -1)
				cmd = s.substring(1);
			else
				cmd = s.substring(1, idx);
			switch (cmd) {
			case "footnote": {
				state.curr = ef.block("footnote");
				Span span = ef.span("footnote-number", Integer.toString(state.nextFootnoteText()) + " ");
				state.curr.addSpan(span);
				break;
			}
			default:
				System.out.println((state.chapter-1) + "." + (state.section-1) + (state.commentary?"c":"") + " handle inline command: " + s);
			}
		} else if (s.startsWith("*")) {
			if (state.curr != null) {
				sink.block(state.curr);
			}
			int idx = s.indexOf(" ");
			if (idx == 1)
				state.curr = ef.block("bullet");
			else
				state.curr = ef.block("bullet" + idx);
			String text = s.substring(idx+1).trim();
			state.curr.addSpan(ef.span("bullet-sign", "\u2022"));
			ProcessingUtils.addSpans(ef, state, state.curr, text);
		} else {
			/*
			if (blockquote) {
				if (curr != null)
					sink.block(curr);
				curr = ef.block("blockquote");
			} else 
			 */
			if (state.curr == null) {
				state.curr = ef.block("text");
			}
			/*
			if (s.startsWith("&link ")) {
				// do we need to add spaces?
				int idx = s.indexOf(' ');
				int idx2 = s.indexOf(' ', idx+1);
				curr.addSpan(ef.span(null, " "));
				curr.addSpan(ef.span("link", s.substring(idx+1, idx2).trim()));
				ProcessingUtils.addSpans(ef, curr, s.substring(idx2+1).trim());
				curr.addSpan(ef.span("endlink", ""));
				curr.addSpan(ef.span(null, " "));
			} else {
			*/
				ProcessingUtils.addSpans(ef, state, state.curr, s);
				/*
			}
			*/
		}
	}
	
	@Override
	protected void endBlock(DocState st) throws IOException {
		if (st.cmd != null) {
			switch(st.cmd.name) {
			case "Chapter": {
				String title = st.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Chapter without title");
				if (st.chapter > 0)
					title = Integer.toString(st.chapter) + " " + title;
				st.chapter++;
				st.section = 1;
				SpanBlock blk = ef.block("chapter-title");
				ProcessingUtils.addSpans(ef, st, blk, title);
				sink.block(blk);
				break;
			}
			case "Section": {
				String title = st.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Section without title");
				if (st.chapter > 1)
					title = Integer.toString(st.chapter-1) + "." + Integer.toString(st.section) + (st.commentary?"c":"") + " " + title;
				st.section++;
				SpanBlock blk = ef.block("section-title");
				ProcessingUtils.addSpans(ef, st, blk, title);
				sink.block(blk);
				break;
			}
			case "Commentary": {
				sink.brk(new CommentaryBreak());
				st.commentary = true;
				st.section = 1;
				break;
			}
			default:
				System.out.println("handle " + st.cmd);
			}
			st.cmd = null;
		} else
			super.endBlock(st);
	}
}
