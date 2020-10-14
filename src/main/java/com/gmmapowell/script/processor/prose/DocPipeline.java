package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.flasck.flas.grammar.Grammar;
import org.flasck.flas.grammar.Production;
import org.zinutils.xml.XML;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.SyncAfterFlow;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.Utils;

public class DocPipeline extends ProsePipeline<DocState> {
	private final DocState state = new DocState();
	private final List<File> samples = new ArrayList<>();
	private final Grammar grammar;
	private final TableOfContents toc;
	
	public DocPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
		File tocfile = null;
		if (options.containsKey("samples"))
			this.samples.add(new File(Utils.subenvs(options.remove("samples"))));
		if (options.containsKey("toc"))
			tocfile = new File(root, Utils.subenvs(options.remove("toc")));
		if (options.containsKey("grammar")) {
			String grammarName = Utils.subenvs(options.remove("grammar"));
			File file = new File(grammarName);
			if (!file.exists())
				throw new RuntimeException("Grammar file " + grammarName + " does not exist");
			this.grammar = Grammar.from(XML.fromFile(file));
		} else
			this.grammar = null;
		toc = new TableOfContents(tocfile);
		state.flows.put("header", new Flow("header", false)); // needs to be a "callback" flow
		state.flows.put("main", new Flow("main", true));
		state.flows.put("footnotes", new Flow("footnotes", true));
		state.flows.put("footer", new Flow("footer", false)); // needs to be a "callback" flow
	}
	
	@Override
	protected DocState begin(String file) {
		state.reset(file);
		return state;
	}

	@Override
	protected void handleLine(DocState state, String s) throws IOException {
		if (s.equals("$$")) {
			state.blockquote = !state.blockquote;
		} else if (s.startsWith("@")) {
			// it's a block starting command
			commitCurrentCommand();
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
			case "tt": {
				state.nestSpan("preformatted");
				state.text(args.toString());
				state.popSpan();
				break;
			}
			case "link": {
				String lk = readString(state, args);
				String tx = readString(state, args);
				state.nestSpan("link");
				ProcessingUtils.process(state, tx);
				state.popSpan();
				// TODO: would something here be better as an operator?
				state.nestSpan("endlink");
				state.popSpan();
				break;
			}
			case "sp": {
				if (state.inPara())
					state.op(new BreakingSpace());
				else {
					state.newPara("text");
				}
				ProcessingUtils.process(state, args.toString().trim());
				break;
			}
			case "footnote": {
				commitCurrentCommand();
				state.switchToFlow("footnotes");
				state.newPara("footnote");
				state.newSpan("footnote-number");
				state.op(new SyncAfterFlow("main"));
				state.text(Integer.toString(state.nextFootnoteText()) + " ");
				break;
			}
			case "include": {
				commitCurrentCommand();
				String file = readString(state, args);
				Map<String, String> params = readParams(state, args, "formatter");
//				System.out.println("want to include " + file + " with " + params);
				File f = null;
				for (File r : samples) {
					File tf = new File(r, file);
					if (tf.isFile() && tf.canRead()) {
						f = tf;
						break;
					}
				}
				if (f == null)
					throw new RuntimeException("cannot find " + file + " in any of " + samples);
				// TODO: we should configure this according to the params, possibly with this as a boring default
				Formatter formatter;
				if (!params.containsKey("formatter"))
					 formatter = new BoringFormatter(state);
				switch (params.get("formatter")) {
				case "html":
					formatter = new HTMLFormatter(state);
					break;
				case "flas":
					formatter = new FLASFormatter(state);
					break;
				default:
					formatter = new BoringFormatter(state);
					break;
				}
				state.inline = new IncludeCommand(state, f, formatter);
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
			case "select": {
				if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
					throw new RuntimeException("&select must immediately follow &include");
				}
				Map<String, String> params = readParams(state, args, "from", "what", "exdent");
				((IncludeCommand)state.inline).selectOnly(params.get("from"), params.get("what"), params.get("exdent"));
				break;
			}
			case "stop": {
				if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
					throw new RuntimeException("&select must immediately follow &include");
				}
				Map<String, String> params = readParams(state, args, "at", "elide");
				((IncludeCommand)state.inline).stopAt(params.get("at"), params.get("elide"));
				break;
			}
			case "indents": {
				if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
					throw new RuntimeException("&select must immediately follow &include");
				}
				Map<String, String> params = readParams(state, args, "from", "to");
				((IncludeCommand)state.inline).indents(Integer.parseInt(params.get("from")), Integer.parseInt(params.get("to")));
				break;
			}
			case "grammar": {
				commitCurrentCommand();
				Map<String, String> params = readParams(state, args, "rule");
				String ruleName = params.get("rule");
				if (debug)
					System.out.println("including grammar for production " + ruleName);
				try {
					Production rule = grammar.findRule(ruleName);
					state.inline = new GrammarCommand(rule, state);
				} catch (RuntimeException ex) {
					System.out.println(state.inputLocation() + ": " + ex.getMessage());
				}
				// TODO: we need the ability to remove rules we don't like
				// In particular, we need to move the Ziniki extensions into their own rules
				// and then hide them in the FLAS manual
				break;
			}
			case "removeOption": {
				if (state.inline == null || !(state.inline instanceof GrammarCommand)) {
					throw new RuntimeException("&removeOption must immediately follow &grammar");
				}
				Map<String, String> params = readParams(state, args, "prod");
//				System.out.println("want to remove from " + state.inline + " with " + params);
				((GrammarCommand)state.inline).removeProd(params.get("prod"));
				break;
			}
			case "review":
				if (args == null)
					throw new RuntimeException("&review command needs something to review");
				System.out.println("review in " + state.location() + ": " + readString(state, args));
				assertArgsDone(args);
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
			int idx = s.indexOf(" ");
			if (idx == 1)
				state.newPara("bullet");
			else
				state.newPara("bullet" + idx);
			state.newSpan("bullet-sign");
			state.text("\u2022");
			state.endSpan();
			ProcessingUtils.process(state, s.substring(idx+1).trim());
		} else {
			if (!state.inPara()) {
				if (state.inRefComment)
					state.newPara("refComment");
				else
					state.newPara("text");
			}
			ProcessingUtils.process(state, s);
		}
		if (state.blockquote)
			state.endPara();
	}
	
	@Override
	protected void commitCurrentCommand() throws IOException {
		if (state.cmd != null) {
			switch(state.cmd.name) {
			case "Chapter": {
				String title = state.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Chapter without title");
				String style = state.cmd.args.get("style");
				if (style == null)
					style = "chapter";
				if (state.chapter > 0) {
					title = Integer.toString(state.chapter) + " " + title;
					state.wantNumbering = true;
				} else {
					state.wantNumbering = false;
				}
				state.newSection("main", style);
				toc.chapter(title);
				state.newPara("chapter-title");
				ProcessingUtils.process(state, title);
				state.endPara();
				
				state.chapter++;
				state.section = 1;
				break;
			}
			case "Section": {
				String title = state.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Section without title");
				if (state.wantNumbering)
					title = Integer.toString(state.chapter-1) + "." + Integer.toString(state.section) + (state.commentary?"c":"") + " " + title;
				toc.section(title);
				state.newPara("section-title");
				ProcessingUtils.process(state, title);
				state.endPara();
				
				state.section++;
				break;
			}
			case "Subsection": {
				String title = state.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Section without title");
				toc.subsection(title);
				state.newPara("subsection-title");
				ProcessingUtils.process(state, title);
				state.endPara();
				break;
			}
			case "Commentary": {
				state.endSpan();
				state.newPara("text");
				state.newSpan();
				state.op(new CommentaryBreak());
				state.endPara();
				state.commentary = true;
				state.section = 1;
				break;
			}
			case "Comment": {
				state.newPara("beginRefComment");
				state.newSpan("comment-sign");
				state.text("\u25A0");
				state.endSpan();
				state.inRefComment = true;
				break;
			}
			case "/": {
				state.newPara("endRefComment");
				state.newSpan("comment-sign");
				state.text("\u25A1");
				state.endSpan();
				state.inRefComment = false;
				break;
			}
			default:
				System.out.println("handle " + state.cmd);
			}
			state.cmd = null;
		} else if (state.inline != null) {
			InlineCommand inline = state.inline;
			state.inline = null;
			inline.execute();
		} else if (state.inPara()) {
			state.endPara();
		}
	}
	
	@Override
	protected void done() {
		for (Entry<String, Flow> e : state.flows.entrySet()) {
			sink.flow(e.getValue());
		}
		try {
			toc.write();
		} catch (Exception ex) {
			System.out.println("Could not write table of contents" + ex.getMessage());
		}
	}
}
