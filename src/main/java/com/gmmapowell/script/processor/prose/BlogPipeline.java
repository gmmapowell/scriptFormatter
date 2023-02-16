package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.zinutils.system.RunProcess;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class BlogPipeline extends ProsePipeline<BlogState> {
	private BlogState state;

	public BlogPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
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
		if (s.equals("$$")) {
			state.blockquote = !state.blockquote;
		} else if (s.startsWith("+")) {
			state.newPara(headingLevel(s));
			ProcessingUtils.process(state, s.substring(s.indexOf(" ")+1).trim());
		} else if (s.startsWith("*")) {
			state.newPara("bullet");
			ProcessingUtils.process(state, s.substring(s.indexOf(" ")+1).trim());
		} else {
			if (state.blockquote) {
				state.newPara("blockquote");
				ProcessingUtils.process(state, s);
				return;
			} else if (!state.inPara()) {
				state.newPara("text");
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
				case "italic":
				case "tt": {
					state.newPara("text", cmd);
					ProcessingUtils.process(state, args.toString());
					break;
				}
				case "link": {
					String lk = readString(state, args);
					String tx = readString(state, args);
					
					if (!state.inPara())
						state.newPara("text");
					if (!state.inSpan())
						state.newSpan();
					state.op(new LinkOp(lk, tx));
					break;
				}
				case "sp": {
					if (state.inSpan())
						state.op(new BreakingSpace());
					break;
				}
				case "img": {
					state.newPara("text");
					state.newSpan();
					// Doing this properly may require another API (picker?)
					// See: https://bloggerdev.narkive.com/SC3HJ3UM/upload-images-using-blogger-api
					// For now, upload the image by hand and put the URL here
					//  Obvs you can also use any absolute URL
					String link = readString(state, args);
					state.op(new ImageOp(link));
					break;
				}
				case "git": {
					String dir = readString(state, args);
					System.out.println("Want to take git imports from " + dir);
					state.gitdir(dir);
					break;
				}
				case "import": {
					try {
						state.newPara("blockquote");
						state.newSpan();
						state.text("this is a test");
						String branch = readString(state, args);
						String filespec = readString(state, args);
						// TODO: this should allow some (optional) range specifier as well, probably substring or regexp
						state.newPara("blockquote");
						state.newSpan();
						state.text("include some part of " + filespec + " from " + branch);
						gitShow(state, branch, filespec);
					} catch (Exception ex) {
						System.err.println(ex.getMessage());
					}
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

	private void gitShow(BlogState state, String branch, String filespec) {
		File dir = state.gitdir();
		if (dir == null)
			throw new RuntimeException("Cannot use &import without &git");
		System.out.println("dir = " + dir);
		RunProcess gitcmd = new RunProcess("git");
		gitcmd.arg("show");
		gitcmd.arg(branch);
		gitcmd.redirectStderr(System.err);
		gitcmd.processStdout(new GitShowProcessor(filespec));
		gitcmd.executeInDir(dir);
		gitcmd.execute();
		System.out.println(gitcmd.getExitCode());
	}

	@Override
	protected void commitCurrentCommand() throws IOException {
		if (state.inPara()) {
			state.endPara();
		}
	}
	
	private String headingLevel(String s) {
		int level = 1;
		while (s.length() > level && s.charAt(level) == '+')
			level++;
		return "h" + level;
	}
	
	@Override
	protected void done() {
//		index.close();
	}
}
