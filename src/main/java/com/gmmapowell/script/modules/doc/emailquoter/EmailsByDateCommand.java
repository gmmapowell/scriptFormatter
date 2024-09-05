package com.gmmapowell.script.modules.doc.emailquoter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class EmailsByDateCommand implements AmpCommandHandler {
	public class EmailByDateComparator implements Comparator<EmailAt> {
		@Override
		public int compare(EmailAt o1, EmailAt o2) {
			return o1.meta().date.compareTo(o2.meta().date);
		}
	}

	private final ConfiguredState state;
	private final EmailConfig cfg;

	public EmailsByDateCommand(EmailConfig cfg, ScannerAmpState q) {
		this.cfg = cfg;
		this.state = q.state();
	}

	@Override
	public String name() {
		return "emailsbydate";
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

		// Rearrange emails by date order ...
		Set<EmailAt> emails = new TreeSet<>(new EmailByDateComparator());
		for (EmailThread t : threads) {
			emails.addAll(t.emails);
		}			
		for (EmailAt a : emails) {
			state.newSection("main", "chapter");
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
		}
	}
}
