package com.gmmapowell.script.modules.emailquoter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineArgsParser;
import com.gmmapowell.script.processor.prose.LineCommand;

public class SnapCommand implements LineCommand {
	private final DocState state;
	private final EmailConfig cfg;
	private final SnapList snapFiles;

	public SnapCommand(EmailConfig cfg, DocState state, LineArgsParser args) {
		this.cfg = cfg;
		this.state = state;
		List<String> files = new ArrayList<>();
		while (args.hasMore()) {
			files.add(args.readArg());
		}
		snapFiles = new SnapList(files);
		args.argsDone();
	}

	@Override
	public void execute() throws IOException {
		AtomicBoolean inPara = new AtomicBoolean(false);
		cfg.mailPara.showSnaps(snapFiles, s -> {
			if (s.trim().length() == 0) {
				// para break
				if (inPara.get()) {
					state.endPara();
					inPara.set(false);
				}
			} else {
				if (!inPara.get()) {
					state.newPara("emailquote");
					inPara.set(true);
				}
				ProcessingUtils.noCommands(state, s);
				state.newSpan();
				state.op(new BreakingSpace());
			}
		});
		state.endPara();
	}

}
