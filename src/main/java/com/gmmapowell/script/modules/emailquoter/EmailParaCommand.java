package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;

import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;

public class EmailParaCommand implements LineCommand {
	private final DocState state;

	public EmailParaCommand(EmailConfig cfg, DocState state, StringBuilder args) {
		this.state = state;
		
	}
	
	@Override
	public void execute() throws IOException {
		state.newPara("blockquote");
		state.newSpan();
		state.text("this is a quoted email");
		state.endPara();
	}
}
