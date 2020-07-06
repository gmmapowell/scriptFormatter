package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class BlogPipeline extends ProsePipeline<BlogState> {
	public BlogPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}

	
	@Override
	protected BlogState begin(String file) {
		return new BlogState(file);
	}

	@Override
	protected void handleLine(BlogState state, String s) throws IOException {
		if (s.equals("$$")) {
			state.blockquote = !state.blockquote;
		} else if (s.startsWith("+")) {
			if (state.curr != null) {
				sink.block(state.curr);
			}
			state.curr = ef.block(headingLevel(s));
			ProcessingUtils.addSpans(ef, state, state.curr, s.substring(s.indexOf(" ")+1).trim());
		} else if (s.startsWith("*")) {
			if (state.curr != null) {
				sink.block(state.curr);
			}
			state.curr = ef.block("bullet");
			state.curr.addSpan(ef.span(null, s.substring(s.indexOf(" ")+1).trim()));
		} else {
			if (state.blockquote) {
				if (state.curr != null)
					sink.block(state.curr);
				state.curr = ef.block("blockquote");
				state.curr.addSpan(ef.span(null, s));
				return;
			} else if (state.curr == null) {
				state.curr = ef.block("text");
			}
			if (s.startsWith("&")) {
				int idx = s.indexOf(" ");
				String cmd;
				StringBuilder args;
				if (idx == -1) {
					cmd = s.substring(1);
					args = null;
				} else {
					cmd = s.substring(1, idx);
					args = new StringBuilder(s.substring(idx+1));
				}
				switch (cmd) {
				case "bold":
				case "italic": {
					if (state.defaultSpans.isEmpty() || !state.defaultSpans.get(0).equals(cmd))
						state.defaultSpans.add(0, cmd);
					else
						state.defaultSpans.remove(0);
					break;
				}
				case "link": {
					String lk = readString(state, args);
					String tx = readString(state, args);
					state.curr.addSpan(ef.span("link", lk));
					ProcessingUtils.addSpans(ef, state, state.curr, tx);
					state.curr.addSpan(ef.span("endlink", ""));
					break;
				}
				case "sp": {
					if (state.curr == null)
						state.curr = ef.block("text");
					state.curr.addSpan(ef.lspan(state.defaultSpans, " "));
					ProcessingUtils.addSpans(ef, state, state.curr, args.toString().trim());
					break;
				}
				case "img": {
					// Doing this properly may require another API (picker?)
					// See: https://bloggerdev.narkive.com/SC3HJ3UM/upload-images-using-blogger-api
					// For now, upload the image by hand and put the URL here
					//  Obvs you can also use any absolute URL
					String link = readString(state, args);
					if (state.curr == null)
						state.curr = ef.block("text");
					state.curr.addSpan(ef.html("<img border='0' src=\'" + link + "' />"));
					break;
				}
				default:
					throw new RuntimeException(state.location() + " handle inline command: " + cmd);
				}
			} else {
				ProcessingUtils.addSpans(ef, state, state.curr, s);
			}
		}
	}

	private String headingLevel(String s) {
		int level = 1;
		while (s.length() > level && s.charAt(level) == '+')
			level++;
		return "h" + level;
	}
}
