package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.util.Map;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.NoSuchCommandException;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.configured.LifecycleObserver;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.sink.Sink;

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
				if (idx == -1) {
					cmd = s.substring(1);
				} else {
					cmd = s.substring(1, idx);
				}
				switch (cmd) {
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
