package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.util.Map;

import org.zinutils.exceptions.InvalidUsageException;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ExtensionPoint;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.configured.LifecycleObserver;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.SBLineArgsParser;

public class DocProcessor extends AtProcessor<DocState> {
	private DocState state;
	
	public DocProcessor(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
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

	private void handleTextLine(DocState state, String s) {
		if (!state.inPara()) {
			else if (this.scanmode == ScanMode.DETAILS && (state.scanMode == ScanMode.OVERVIEW || state.scanMode == ScanMode.CONCLUSION))
				state.newPara("text", "bold");
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
	}
	
	@Override
	protected void postRender() {
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
