package com.gmmapowell.script.modules.doc.emailquoter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class SnapCommand implements AmpCommandHandler {
	private final ConfiguredState state;
	private final EmailConfig cfg;

	public SnapCommand(EmailConfig cfg, ScannerAmpState state) {
		this.cfg = cfg;
		this.state = state.state();
	}

	@Override
	public String name() {
		return "snap";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		List<String> files = new ArrayList<>();
		while (cmd.args.hasMore()) {
			files.add(cmd.args.readArg());
		}
		cmd.args.argsDone();
		SnapList snapFiles = new SnapList(files);
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
					state.noCommandsText(((SnapRef)s).ref);
					state.endPara();
				} else if (s instanceof SnapUser) {
					if (inPara.get()) {
						state.endPara();
						inPara.set(false);
					}
					state.newPara("emailquote", "bold");
					state.noCommandsText(((SnapUser)s).snapper);
					state.endPara();
				} else {
					if (!inPara.get()) {
						state.newPara("emailquote");
						inPara.set(true);
					} else {
						state.newSpan();
						state.op(new BreakingSpace());
					}
					state.noCommandsText(((SnapText)s).text);
				}
			}
		});
		state.endPara();
	}

}
