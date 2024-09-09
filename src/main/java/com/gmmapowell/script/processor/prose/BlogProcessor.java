package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.zinutils.system.RunProcess;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.NoSuchCommandException;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.configured.LifecycleObserver;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.SBLineArgsParser;

public class BlogProcessor extends ProseProcessor<BlogState> {
	private BlogState state;

	public BlogProcessor(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}
	
	@Override
	protected BlogState begin(Map<String, Flow> flows, String file) {
		file = file.replace(".txt", "");
		state = new BlogState(flows, file);
		state.flows.put(file, new Flow(file, true));
		state.newSection(file, "blog");
		return state;
	}

	@Override
	protected void handleLine(BlogState state, String s) throws IOException {
		if (s.trim().equals("$$")) {
			state.blockquote = !state.blockquote;
		} else {
			if (state.blockquote) {
				state.newPara("blockquote");
				try {
					ProcessingUtils.process(state, s);
				} catch (NoSuchCommandException ex) {
					throw ex.context("blockquote");
				}
				return;
			} else if (!state.inPara()) {
				state.newPara("text");
			}
			if (s.startsWith("&")) {
				int idx = s.indexOf(" ");
				String cmd;
				LineArgsParser p = null;
				if (idx == -1) {
					cmd = s.substring(1);
				} else {
					cmd = s.substring(1, idx);
					p = new SBLineArgsParser<BlogState>(state, s.substring(idx+1));
				}
				switch (cmd) {
				case "sp": {
					if (state.inSpan())
						state.op(new BreakingSpace());
					if (p != null)
						ProcessingUtils.process(state, p.asString());
					break;
				}
				case "includeTag": {
					String tag = p.readString();
//					System.out.println("Want to take git includes from tag " + tag);
					state.gittag(tag);
					break;
				}
				case "include": {
					commitCurrentCommand();
					String file = p.readString();
					Map<String, String> params = p.readParams("formatter");
					File f = new File(file);
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
					state.include = new IncludeCommand(state, f, formatter);
					break;
				}
				case "remove": {
					if (state.include == null) {
						throw new RuntimeException("&remove must immediately follow &include");
					}
					Map<String, String> params = p.readParams("from", "what");
//					System.out.println("want to remove from " + state.inline + " with " + params);
					((IncludeCommand)state.include).butRemove(params.get("from"), params.get("what"));
					break;
				}
				case "select": {
					if (state.include == null) {
						throw new RuntimeException("&select must immediately follow &include");
					}
					Map<String, String> params = p.readParams("from", "what", "exdent");
					((IncludeCommand)state.include).selectOnly(params.get("from"), params.get("what"), params.get("exdent"));
					break;
				}
				case "stop": {
					if (state.include == null) {
						throw new RuntimeException("&stop must immediately follow &include");
					}
					Map<String, String> params = p.readParams("at", "elide");
					((IncludeCommand)state.include).stopAt(params.get("at"), params.get("elide"));
					break;
				}
				case "indents": {
					if (state.include == null) {
						throw new RuntimeException("&indents must immediately follow &include");
					}
					Map<String, String> params = p.readParams("from", "to");
					((IncludeCommand)state.include).indents(Integer.parseInt(params.get("from")), Integer.parseInt(params.get("to")));
					break;
				}
				case "needbreak": {
					commitCurrentCommand();
					state.newPara("break");
					break;
				}
				default:
					throw new RuntimeException(state.inputLocation() + " handle inline command: " + cmd);
				}
			} else {
				if (!state.inPara())
					state.newPara("text");
				ProcessingUtils.process(state, s);
			}
		}
	}

	private void gitShow(BlogState state, String branch, String filespec, String from, String to) {
		File dir = state.gitdir();
		if (dir == null)
			throw new RuntimeException("Cannot use &import without &git");
		System.out.println("git show " + dir + " " + branch + " " + filespec + " " + from + " => " + to);
		RunProcess gitcmd = new RunProcess("git");
		gitcmd.arg("show");
		gitcmd.arg(branch);
		gitcmd.redirectStderr(System.err);
		gitcmd.processStdout(new GitShowProcessor(state, branch, filespec, from, to));
		gitcmd.executeInDir(dir);
		gitcmd.execute();
	}

	@Override
	protected void commitCurrentCommand() throws IOException {
		if (state.inPara()) {
			state.endPara();
		}
		if (state.include != null) {
			IncludeCommand include = state.include;
			state.include = null;
			include.execute();
		}
	}
	
	private String headingLevel(String s) {
		int level = 1;
		while (s.length() > level && s.charAt(level) == '+')
			level++;
		return "h" + level;
	}
	
	@Override
	protected void done() throws IOException {
		if (state != null)
			commitCurrentCommand();
		
		// TODO: I just hacked this in here, but I think it should be a separate phase
		for (Flow f : state.flows.values())
			sink.flow(f);
//		index.close();
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
	

	@Override
	public void lifecycleObserver(LifecycleObserver observer) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void allDone() {
		// TODO Auto-generated method stub
		
	}
}
