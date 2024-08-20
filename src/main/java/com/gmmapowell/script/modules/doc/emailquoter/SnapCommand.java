package com.gmmapowell.script.modules.doc.emailquoter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.LineCommand;
import com.gmmapowell.script.utils.LineArgsParser;

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
			if (s instanceof SnapPara) {
				// para break
				if (inPara.get()) {
					state.endPara();
					inPara.set(false);
				}
			} else {
				if (s instanceof SnapRef) {
					if (inPara.get()) {
						state.endPara();
						inPara.set(false);
					}
					state.newPara("emailquote", "italic");
					ProcessingUtils.noCommands(state, ((SnapRef)s).ref);
					state.endPara();
				} else if (s instanceof SnapUser) {
					if (inPara.get()) {
						state.endPara();
						inPara.set(false);
					}
					state.newPara("emailquote", "bold");
					ProcessingUtils.noCommands(state, ((SnapUser)s).snapper);
					state.endPara();
				} else {
					if (!inPara.get()) {
						state.newPara("emailquote");
						inPara.set(true);
					} else {
						state.newSpan();
						state.op(new BreakingSpace());
					}
					ProcessingUtils.noCommands(state, ((SnapText)s).text);
				}
			}
		});
		state.endPara();
	}

}
