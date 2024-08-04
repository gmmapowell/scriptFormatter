package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;

import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;

public class SnapCommand implements LineCommand {

	private final DocState state;
	private EmailConfig cfg;

	public SnapCommand(EmailConfig cfg, DocState state, StringBuilder args) {
		this.cfg = cfg;
		this.state = state;
	}

	@Override
	public void execute() throws IOException {
		state.newPara("emailquote");
		state.newSpan();
		ProcessingUtils.process(state, "this is a quoted snap from " + cfg.snaps);
		state.endPara();
	}

}
