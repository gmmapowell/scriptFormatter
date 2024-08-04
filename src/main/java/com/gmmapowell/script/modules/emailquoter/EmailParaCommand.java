package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineArgsParser;
import com.gmmapowell.script.processor.prose.LineCommand;

public class EmailParaCommand implements LineCommand {
	private final EmailConfig cfg;
	private final DocState state;
	private final Citation citation;

	public EmailParaCommand(EmailConfig cfg, DocState state, LineArgsParser args) {
		this.cfg = cfg;
		this.state = state;
		String message = args.readArg();
		String quoted = args.readArg();
		this.citation = Citation.parse(message, quoted);
		args.argsDone();
	}
	
	@Override
	public void execute() throws IOException {
		state.newPara("emailquote");
		state.newSpan();
		cfg.mailPara.quoteEmail(citation, s -> {
			ProcessingUtils.noCommands(state, s);
			state.newSpan();
			state.op(new BreakingSpace());
		});
		state.endPara();
	}
}
