package com.gmmapowell.script.modules.processors.blog.diagram;

import java.io.File;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.modules.processors.blog.UploadAll;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class DiagramSpotter implements ProcessingScanner {
	private final static String WEB_PATH = System.getenv("DIAGRAMMER_WEBROOT");
	private final DiagramState diagram;
	private ConfiguredState state;

	public DiagramSpotter(ConfiguredState state) {
		this.state = state;
		diagram = state.require(DiagramState.class);
		diagram.provideUploaders(state.global().requireState(UploadAll.class));
	}
	
	@Override
	public boolean wantTrimmed() {
		return false;
	}

	@Override
	public boolean handleLine(String s) {
		if (diagram.isActive()) {
			if (s.trim().equals("@/")) {
				diagram.draw();
			} else
				diagram.add(s);
			return true;
		} else if (s.trim().equals("@Diagram")) {
			if (WEB_PATH == null || !new File(WEB_PATH).isDirectory()) {
				throw new CantHappenException("there is no env var DIAGRAMMER_WEBROOT");
			}
			diagram.start(state, WEB_PATH);
			return true; // it has been handled
		} else {
			return false;
		}
	}
}
