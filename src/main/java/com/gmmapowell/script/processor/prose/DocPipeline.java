package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class DocPipeline extends ProsePipeline<DocState> {
	private final DocState root = new DocState();
	private List<File> roots = new ArrayList<>();
	
	public DocPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
		// TODO: this needs to come from "options"
		this.roots.add(new File("/Users/gareth/Ziniki/Over/Demos/samples"));
	}
	
	@Override
	protected DocState begin(String file) {
		root.reset(file);
		return root;
	}

	@Override
	protected void handleLine(DocState state, String s) throws IOException {
		if (s.startsWith("@")) {
			// it's a block starting command
			endBlock(state);
			state.cmd = new DocCommand(s.substring(1));
//			System.out.println((state.chapter-1) + "." + (state.section-1) + (state.commentary?"c":"") + " @: " + s);
		} else if (state.cmd != null) {
			int pos = s.indexOf('=');
			if (pos == -1)
				throw new RuntimeException("invalid argument to " + state.cmd + ": " + s);
			state.cmd.arg(s.substring(0, pos).trim(), s.substring(pos+1).trim());
		} else if (s.startsWith("&")) {
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
			case "tt":
				if (state.curr == null)
					state.curr = ef.block("preformatted");
				else
					throw new RuntimeException("you need to set up to do preformatted text in an existing block");
				state.curr.addSpan(ef.span(null, args.toString()));
				break;
			case "footnote": {
				endBlock(state);
				state.curr = ef.block("footnote");
				Span span = ef.span("footnote-number", Integer.toString(state.nextFootnoteText()) + " ");
				state.curr.addSpan(span);
				break;
			}
			case "sp": {
				if (state.curr == null)
					state.curr = ef.block("text");
				state.curr.addSpan(ef.span(null, " "));
				ProcessingUtils.addSpans(ef, state, state.curr, args.toString().trim());
				break;
			}
			case "include": {
				endBlock(state);
				String file = readString(state, args);
				Map<String, String> params = readParams(state, args, "formatter");
//				System.out.println("want to include " + file + " with " + params);
				File f = null;
				for (File r : roots) {
					File tf = new File(r, file);
					if (tf.isFile() && tf.canRead()) {
						f = tf;
						break;
					}
				}
				if (f == null)
					throw new RuntimeException("cannot find " + file + " in any of " + roots);
				// TODO: we should configure this according to the params, possibly with this as a boring default
				Formatter formatter;
				if (!params.containsKey("formatter"))
					 formatter = new BoringFormatter();
				switch (params.get("formatter")) {
				case "html":
					formatter = new HTMLFormatter();
					break;
				case "flas":
					formatter = new FLASFormatter();
					break;
				default:
					formatter = new BoringFormatter();
					break;
				}
				state.inline = new IncludeCommand(f, formatter);
				break;
			}
			case "remove": {
				if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
					throw new RuntimeException("&remove must immediately follow &include");
				}
				Map<String, String> params = readParams(state, args, "from", "what");
//				System.out.println("want to remove from " + state.inline + " with " + params);
				((IncludeCommand)state.inline).butRemove(params.get("from"), params.get("what"));
				break;
			}
			case "review":
				if (args == null)
					throw new RuntimeException("&review command needs something to review");
				System.out.println("review '" + readString(state, args) + "' in " + state.location());
				break;
			case "future":
				if (args == null)
					throw new RuntimeException("&future command needs a comment");
				System.out.println(state.location() + ": in the future, " + readString(state, args));
				break;
			case "morework":
				if (args == null)
					throw new RuntimeException("&morework command needs a description");
				System.out.println("more work is required at " + state.location() + ": " + readString(state, args));
				break;
			case "outrageousclaim":
				System.out.println("There is an outrageous claim at " + state.location());
				break;
			default:
				throw new RuntimeException(state.location() + " handle inline command: " + s);
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
			if (state.curr == null) {
				state.curr = ef.block("text");
			}
			ProcessingUtils.addSpans(ef, state, state.curr, s);
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
		} else if (st.inline != null) {
			st.inline.execute(sink, ef);
			st.inline = null;
		} else
			super.endBlock(st);
	}
}
