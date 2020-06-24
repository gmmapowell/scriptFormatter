package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class DocPipeline extends ProsePipeline<DocState> {
	public DocPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}
	
	@Override
	protected DocState begin() {
		return new DocState();
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
		if (s.startsWith("*")) {
			if (state.curr != null) {
				sink.block(state.curr);
			}
			state.curr = ef.block("bullet");
			state.curr.addSpan(ef.span(null, s.substring(s.indexOf(" ")+1).trim()));
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
				ProcessingUtils.addSpans(ef, state.curr, s);
				/*
			}
			*/
		}
	}
}
