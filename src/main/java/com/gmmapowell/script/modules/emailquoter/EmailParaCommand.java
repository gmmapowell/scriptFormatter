package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;

import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;

public class EmailParaCommand implements LineCommand {
	private final EmailConfig cfg;
	private final DocState state;

	public EmailParaCommand(EmailConfig cfg, DocState state, StringBuilder args) {
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
