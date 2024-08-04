package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
		AtomicBoolean inPara = new AtomicBoolean(false);
		cfg.mailPara.quoteEmail(citation, s -> {
			if (s.trim().length() == 0) {
				if (inPara.get()) {
					state.endPara();
					inPara.set(false);
				}
			} else {
				if (!inPara.get()) {
					state.newPara("emailquote");
					state.newSpan();
					inPara.set(true);
				} else {
					state.newSpan();
					state.op(new BreakingSpace());
				}
				ProcessingUtils.noCommands(state, s);
			}
		});
		if (inPara.get())
			state.endPara();
	}
}
