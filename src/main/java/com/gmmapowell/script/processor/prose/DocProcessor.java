package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.flasck.flas.grammar.Grammar;
import org.flasck.flas.grammar.Production;
import org.zinutils.exceptions.InvalidUsageException;
import org.zinutils.exceptions.WrappedException;
import org.zinutils.xml.XML;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.LinkFromRef;
import com.gmmapowell.script.flow.LinkFromTOC;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.processor.prose.DocState.ScanMode;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.SBLineArgsParser;
import com.gmmapowell.script.utils.Utils;

public class DocProcessor extends AtProcessor<DocState> {
	private DocState state;
	private final List<File> samples = new ArrayList<>();
	private final Grammar grammar;
	private final TableOfContents toc;
	private final JSONObject currentMeta;
	
	public DocProcessor(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
		Place tocfile = null;
		Place metafile = null;
		if (options.containsKey("samples"))
			this.samples.add(new File(Utils.subenvs(options.remove("samples"))));
		if (options.containsKey("toc"))
			tocfile = root.place(options.remove("toc"));
		if (options.containsKey("meta"))
			metafile = root.ensurePlace(options.remove("meta"));
		if (options.containsKey("grammar")) {
			String grammarName = Utils.subenvs(options.remove("grammar"));
			File file = new File(grammarName);
			if (!file.exists())
				throw new RuntimeException("Grammar file " + grammarName + " does not exist");
			this.grammar = Grammar.from(XML.fromFile(file));
		} else
			this.grammar = null;
		toc = new TableOfContents(tocfile, metafile);
		if (metafile != null && metafile.exists()) {
			try {
				currentMeta = GeoFSUtils.readJSON(metafile);
			} catch (JSONException e) {
				throw new ConfigException("Failed to read " + metafile + ": " + e);
			}
		} else {
			currentMeta = null;
		}
	}
	
	@Override
	protected DocState begin(Map<String, Flow> flows, String file) {
		System.out.println("processing input file: " + file);
		if (state == null) {
			this.state = new DocState(flows);
			state.flows.put("header", new Flow("header", false)); // needs to be a "callback" flow
			state.flows.put("main", new Flow("main", true));
			state.flows.put("footnotes", new Flow("footnotes", true));
			state.flows.put("footer", new Flow("footer", false)); // needs to be a "callback" flow
		}
		state.newfile(file);
		return state;
	}

	@Override
	protected void handleLine(DocState state, String s) throws IOException {
		// Skip everything in the "DETAILS" section
		if (!"@Conclusion".equals(s) && this.scanmode == ScanMode.OVERVIEW && (state.scanMode == ScanMode.DETAILS))
			return;
		
		if (commonHandleLine(state, s))
			return;
		else if (s.startsWith("&")) {
			handleSingleLineCommand(state, s);
		} else {
			handleTextLine(state, s);
		}
		if (state.blockquote)
			state.endPara();
	}

	private void handleTextLine(DocState state, String s) {
		if (!state.inPara()) {
			if (state.blockquote)
				state.newPara("blockquote");
			else if (state.inRefComment)
				state.newPara("refComment");
			else if (this.scanmode == ScanMode.DETAILS && (state.scanMode == ScanMode.OVERVIEW || state.scanMode == ScanMode.CONCLUSION))
				state.newPara("text", "bold");
			else
				state.newPara("text");
		} else if (joinspace) {
			if (!state.inSpan())
				state.newSpan();
			state.op(new BreakingSpace());
		}
		ProcessingUtils.process(state, s);
	}

	private void handleSingleLineCommand(DocState state, String s) throws IOException {
		int idx = s.indexOf(" ");
		String cmd;
		LineArgsParser p = null;
		if (idx == -1) {
			cmd = s.substring(1);
			p = new SBLineArgsParser<DocState>(state, "");
		} else {
			cmd = s.substring(1, idx);
			p = new SBLineArgsParser<DocState>(state, s.substring(idx+1));
		}
		switch (cmd) {
		case "bold":
		case "italic": {
			if (!state.inPara())
				state.newPara("text");
			if (!state.inSpan())
				state.newSpan();
			state.nestSpan(cmd);
			ProcessingUtils.processPart(state, p.asString(), 0, p.asString().length());
			state.popSpan();
			break;
		}
		case "sp": {
			if (state.inPara()) {
				if (!state.inSpan())
					state.newSpan();
				state.op(new BreakingSpace());
			} else {
				state.newPara("text");
			}
			ProcessingUtils.process(state, p.asString().trim());
			break;
		}
		case "include": {
			commitCurrentCommand();
			String file = p.readString();
			Map<String, String> params = p.readParams("formatter");
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
			Map<String, String> params = p.readParams("from", "what");
//				System.out.println("want to remove from " + state.inline + " with " + params);
			((IncludeCommand)state.inline).butRemove(params.get("from"), params.get("what"));
			break;
		}
		case "select": {
			if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
				throw new RuntimeException("&select must immediately follow &include");
			}
			Map<String, String> params = p.readParams("from", "what", "exdent");
			((IncludeCommand)state.inline).selectOnly(params.get("from"), params.get("what"), params.get("exdent"));
			break;
		}
		case "stop": {
			if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
				throw new RuntimeException("&stop must immediately follow &include");
			}
			Map<String, String> params = p.readParams("at", "elide");
			((IncludeCommand)state.inline).stopAt(params.get("at"), params.get("elide"));
			break;
		}
		case "indents": {
			if (state.inline == null || !(state.inline instanceof IncludeCommand)) {
				throw new RuntimeException("&indents must immediately follow &include");
			}
			Map<String, String> params = p.readParams("from", "to");
			((IncludeCommand)state.inline).indents(Integer.parseInt(params.get("from")), Integer.parseInt(params.get("to")));
			break;
		}
		case "grammar": {
			commitCurrentCommand();
			Map<String, String> params = p.readParams("rule");
			String ruleName = params.get("rule");
			if (ruleName == null) {
				state.inline = new GrammarCommand(grammar, state);
			} else {
				if (debug)
					System.out.println("including grammar for production " + ruleName);
				try {
					Production rule = grammar.findRule(ruleName);
					state.inline = new GrammarCommand(rule, state);
				} catch (RuntimeException ex) {
					System.out.println(state.inputLocation() + ": " + ex.getMessage());
				}
			}
			break;
		}
		case "removeOption": {
			if (state.inline == null || !(state.inline instanceof GrammarCommand)) {
				throw new RuntimeException("&removeOption must immediately follow &grammar");
			}
			Map<String, String> params = p.readParams("prod");
//				System.out.println("want to remove from " + state.inline + " with " + params);
			((GrammarCommand)state.inline).removeProd(params.get("prod"));
			break;
		}
		case "morework":
			if (!p.hasMore())
				throw new RuntimeException("&morework command needs a description");
			System.out.println("more work is required at " + state.inputLocation() + ": " + p.readString());
			break;
		case "number": {
			if (!state.activeNumbering())
				throw new InvalidUsageException("cannot use &number outside @Numbering...@/");
			state.newPara(state.numberPara());
			state.newSpan("bullet-sign");
			state.text(state.currentNumber());
			state.endSpan();
			ProcessingUtils.process(state, p.asString().trim());
			break;
		}
		case "ref": {
			// TODO: formatting should be customizable
			if (!p.hasMore())
				throw new RuntimeException("&ref command needs a reference");
			if (!state.inSpan())
				state.newSpan();
			String tx = "unref";
			String anchor = p.readString();
			if (currentMeta != null) {
				try {
					JSONObject anchors = currentMeta.getJSONObject("anchors");
					if (anchors.has(anchor)) {
						JSONObject anch = anchors.getJSONObject(anchor);
						if (!anch.has("number"))
							throw new InvalidUsageException("the anchor '" + anchor + "' does not have a section number");
						tx = anch.getString("number");
					} else {
						System.out.println("there is no anchor '" + anchor + "'");
					}
				} catch (JSONException e) {
					throw WrappedException.wrap(e);
				}
			}
			state.op(new LinkFromRef(toc, anchor, '§' + tx));
			break;
		}
		default: {
			if (!handleConfiguredSingleLineCommand(state, cmd, p)) {
				System.out.println("cannot handle " + s + " at " + state.inputLocation());
			}
			break;
		}
		}
	}

	@Override
	protected void commitCurrentCommand() throws IOException {
		if (state.cmd != null) {
			switch(state.cmd.name) {
			case "Overview": {
				state.scanMode = ScanMode.OVERVIEW;
				break;
			}
			case "Details": {
				state.scanMode = ScanMode.DETAILS;
				break;
			}
			case "Conclusion": {
				state.scanMode = ScanMode.CONCLUSION;
				state.newPara("section-title");
				ProcessingUtils.process(state, "Conclusions");
				state.endPara();
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
			case "Numbering": {
				// TODO: probably should allow a numbering format argument & start value
				state.pushNumbering("arabic", 1);
				break;
			}
			case "/": {
				// We probably actually want to have a stack of what to undo ...
				if (state.inRefComment) {
					state.newPara("endRefComment");
					state.newSpan("comment-sign");
					state.text("\u25A1");
					state.endSpan();
					state.inRefComment = false;
				} else {
					state.popNumbering();
				}
				break;
			}
			case "TOC": {
				if (currentMeta == null)
					break;
				List<LinkFromTOC> links = new ArrayList<>();
				try {
					JSONArray order = currentMeta.getJSONArray("toc");
					JSONObject headings = currentMeta.getJSONObject("headings");
					for (int i=0;i<order.length();i++) {
						Object e = order.get(i);
						if (e instanceof String)
							e = headings.getJSONObject((String)e);
						JSONObject entry = (JSONObject) e;
//						String type = entry.getString("type");
						state.newPara("text"); // "tocline", "toc-" + type
						if (entry.has("number")) {
							state.newSpan(); // tocnumber
							state.text(entry.getString("number"));
							state.op(new BreakingSpace()); // NBSP?
						}
						state.newSpan(); // tocheading
						state.text(entry.getString("title"));
						state.newSpan(); // tocdots - how do we set the width of this?
						state.text("...");
						state.newSpan(); // tocpage - right justified
						LinkFromTOC lk = new LinkFromTOC(entry.getString("page"), entry.getString("title"));
						links.add(lk);
						state.op(lk);
						state.endPara();
					}
					this.toc.links(links);
				} catch (JSONException e) {
					throw WrappedException.wrap(e);
				}
				break;
			}
			default:
				System.out.println("cannot commit " + state.cmd + " at " + state.inputLocation());
				break;
			}
			state.cmd = null;
		} else if (state.inline != null) {
			LineCommand inline = state.inline;
			state.inline = null;
			inline.execute();
		} else if (state.inPara()) {
			state.endPara();
		}
	}
	
	@Override
	protected void done() {
		if (state == null) {
			// we did nothing
			return;
		}
		if (state.inRefComment)
			throw new RuntimeException("Ended in Ref Comment");
		if (state.activeNumbering())
			throw new RuntimeException("Still in numbering block");
	}
	
	@Override
	protected void postRender() {
		try {
			toc.write();
		} catch (Exception ex) {
			System.out.println("Could not write table of contents" + ex.getMessage());
		}
	}

	@Override
	public void addScanner(Class<? extends ProcessingScanner> scanner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends ExtensionPoint, Z extends T, Q> void addExtension(Class<T> ep, Creator<Z, Q> impl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends ExtensionPoint, Z extends T> void addExtension(Class<T> ep, Class<Z> impl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GlobalState global() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
