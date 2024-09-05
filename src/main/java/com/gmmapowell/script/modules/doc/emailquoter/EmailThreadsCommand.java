package com.gmmapowell.script.modules.doc.emailquoter;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class EmailThreadsCommand implements AmpCommandHandler {
	private final ConfiguredState state;
	private final EmailConfig cfg;

	public EmailThreadsCommand(EmailConfig cfg, ScannerAmpState q) {
		this.cfg = cfg;
		this.state = q.state();
	}

	@Override
	public String name() {
		return "emailthreads";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		Set<EmailThread> threads = new TreeSet<>();
		cfg.threads.regions(r -> {
			EmailThread t = new EmailThread(r);
			threads.add(t);
			r.regions(mr -> {
				t.add(new EmailAt(mr));
			});
		});

		for (EmailThread t : threads) {
			state.newSection("main", "chapter");
			state.newPara("chapter-title");
			state.newSpan();
			state.noCommandsText(t.name().substring(10));
			state.endPara();
			
			boolean isFirst = true;
			for (EmailAt a : t.emails) {
				if (!isFirst) {
					state.newSection("main", "chapter");
				}
				state.newPara("section-title");
				state.newSpan();
				state.noCommandsText(a.name());
				state.endPara();

				EmailMeta meta = a.meta();
				state.newPara("default", "tt");
				state.newSpan();
				state.noCommandsText("Date: " + meta.date);
				state.endPara();
				state.newPara("default", "tt");
				state.newSpan();
				state.noCommandsText("From: " + meta.from);
				state.endPara();
				state.newPara("default", "tt");
				state.newSpan();
				state.noCommandsText("Subject: " + meta.subject);
				state.endPara();

				AtomicBoolean aborted = new AtomicBoolean(false);
				a.text(s -> {
					s = s.trim();
					if ((s.startsWith("On ") || s.startsWith("> On")) && s.endsWith(" wrote:")) {
						aborted.set(true);
					}
					if (aborted.get())
						return;
					state.newPara("emailquote");
					state.newSpan();
					state.noCommandsText(s);
					state.endPara();
				});
				
				isFirst = false;
			}
		}
	}
}
