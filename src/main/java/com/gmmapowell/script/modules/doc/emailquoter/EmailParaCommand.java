package com.gmmapowell.script.modules.doc.emailquoter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class EmailParaCommand implements AmpCommandHandler {
	private final EmailConfig cfg;
	private final ConfiguredState state;

	public EmailParaCommand(EmailConfig cfg, ScannerAmpState quelle) {
		this.cfg = cfg;
		this.state = quelle.state();
	}

	@Override
	public String name() {
		return "emailpara";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		try {
			String message = cmd.args.readArg();
			String quoted = cmd.args.readArg();
			cmd.args.argsDone();
			Citation citation = Citation.parse(message, quoted);
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
					state.noCommandsText(s);
				}
			});
			if (inPara.get())
				state.endPara();
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
