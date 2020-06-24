package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	protected DocState begin() {
		root.reset();
		return root;
	}

	@Override
	protected void handleLine(DocState state, String s) throws IOException {
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
			StringBuilder args;
			if (idx == -1) {
				cmd = s.substring(1);
				args = null;
			} else {
				cmd = s.substring(1, idx);
				args = new StringBuilder(s.substring(idx+1));
			}
			switch (cmd) {
			case "footnote": {
				if (state.curr != null) {
					sink.block(state.curr);
					state.curr = null;
				}
				state.curr = ef.block("footnote");
				Span span = ef.span("footnote-number", Integer.toString(state.nextFootnoteText()) + " ");
				state.curr.addSpan(span);
				break;
			}
			case "include": {
				if (state.curr != null) {
					sink.block(state.curr);
					state.curr = null;
				}
				String file = readString(args);
				Map<String, String> params = readParams(args, "formatter");
				System.out.println("want to include " + file + " with " + params);
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
				Formatter formatter = new BoringFormatter();
				try (LineNumberReader lnr = new LineNumberReader(new FileReader(f, Charset.forName("UTF-8")))) {
					String fs;
					while ((fs = lnr.readLine()) != null) {
						sink.block(formatter.format(ef, fs));
					}
				}
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
			if (state.curr == null) {
				state.curr = ef.block("text");
			}
			ProcessingUtils.addSpans(ef, state, state.curr, s);
		}
	}
	
	private String readString(StringBuilder args) {
		if (args == null || args.length() == 0)
			throw new RuntimeException("cannot read from empty string");
		while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
			args.delete(0, 1);
		if (args.length() == 0)
			throw new RuntimeException("cannot read from empty string");
		char c = args.charAt(0);
		if (c != '\'' && c != '"')
			throw new RuntimeException("unquoted string");
		args.delete(0, 1);
		String ret = null;
		for (int i=0;i<args.length();i++) {
			if (args.charAt(i) == c) {
				ret = args.substring(0, i);
				args.delete(0, i+1);
				break;
			}
		}
		if (ret == null)
			throw new RuntimeException("unterminated string");
		return ret;
	}
	
	private Map<String, String> readParams(StringBuilder args, String... allowedStrings) {
		Map<String, String> ret = new TreeMap<>();
		if (args == null)
			return ret;
		List<String> allowed = Arrays.asList(allowedStrings);
		while (args.length() > 0) {
			while (args.length() > 0 && Character.isWhitespace(args.charAt(0)))
				args.delete(0, 1);
			if (args.length() == 0)
				break;
			int j=0;
			while (j < args.length() && args.charAt(j) != '=')
				j++;
			if (j == args.length())
				throw new RuntimeException("needed =");
			String var = args.substring(0, j);
			args.delete(0, j+1);
			if (ret.containsKey(var))
				throw new RuntimeException("duplicate definition of " + var);
			else if (!allowed.contains(var))
				throw new RuntimeException("unexpected definition of " + var + "; allowed = " + allowed);
			else
				ret.put(var, readString(args)); 
		}
		return ret;
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
