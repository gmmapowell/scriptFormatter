package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;

import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;

public class EmailThreadsCommand implements LineCommand {
	private final DocState state;
	private final EmailConfig cfg;

	public EmailThreadsCommand(EmailConfig cfg, DocState state, StringBuilder args) {
		this.cfg = cfg;
		this.state = state;
	}

	@Override
	public void execute() throws IOException {
		state.newPara("emailquote");
		state.newSpan();
		ProcessingUtils.process(state, "this is a quoted email from " + cfg.threads);
		state.endPara();
	}
}
