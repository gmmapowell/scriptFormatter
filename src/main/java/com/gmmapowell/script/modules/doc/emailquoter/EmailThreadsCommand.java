package com.gmmapowell.script.modules.doc.emailquoter;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;
import com.gmmapowell.script.utils.LineArgsParser;

public class EmailThreadsCommand implements LineCommand {
	private final DocState state;
	private final EmailConfig cfg;

	public EmailThreadsCommand(EmailConfig cfg, DocState state, LineArgsParser args) {
		this.cfg = cfg;
		this.state = state;
		args.argsDone();
	}

	@Override
	public void execute() throws IOException {
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
			ProcessingUtils.noCommands(state, t.name().substring(10));
			state.endPara();
			
			boolean isFirst = true;
			for (EmailAt a : t.emails) {
				if (!isFirst) {
					state.newSection("main", "chapter");
				}
				state.newPara("section-title");
				state.newSpan();
				ProcessingUtils.noCommands(state, a.name());
				state.endPara();

				EmailMeta meta = a.meta();
				state.newPara("default", "tt");
				state.newSpan();
				ProcessingUtils.noCommands(state, "Date: " + meta.date);
				state.endPara();
				state.newPara("default", "tt");
				state.newSpan();
				ProcessingUtils.noCommands(state, "From: " + meta.from);
				state.endPara();
				state.newPara("default", "tt");
				state.newSpan();
				ProcessingUtils.noCommands(state, "Subject: " + meta.subject);
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
					ProcessingUtils.noCommands(state, s);
					state.endPara();
				});
				
				isFirst = false;
			}
		}
	}
}
