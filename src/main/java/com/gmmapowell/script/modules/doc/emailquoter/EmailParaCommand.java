package com.gmmapowell.script.modules.doc.emailquoter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;
import com.gmmapowell.script.utils.LineArgsParser;

public class EmailParaCommand implements LineCommand, AmpCommandHandler {
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
	public String name() {
		return "emailpara";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		throw new NotImplementedException(); // but obvs wants most of the code from below
	}
	
	public void execute() throws IOException {
		AtomicBoolean inPara = new AtomicBoolean(false);
		cfg.mailPara.quoteEmail(citation, (n, s) -> {
			s = s.trim();
			if (n == citation.first) {
				if (citation.getFromPhrase() != null) {
					int idx = s.indexOf(citation.getFromPhrase());
					if (idx != -1)
						s = s.substring(idx);
					else
						System.out.println("Could not find text '" + citation.getFromPhrase() + "' at line " + n + " of " + citation.file);
				}
			}
			if (n == citation.last) {
				if (citation.getToPhrase() != null) {
					int idx = s.lastIndexOf(citation.getToPhrase());
					if (idx != -1)
						s = s.substring(0, idx + citation.getToPhrase().length());
					else
						System.out.println("Could not find text '" + citation.getToPhrase() + "' at line " + n + " of " + citation.file);
				}
			}
			if (s.length() == 0) {
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
