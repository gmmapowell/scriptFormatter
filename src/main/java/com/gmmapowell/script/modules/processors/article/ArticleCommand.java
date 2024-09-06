package com.gmmapowell.script.modules.processors.article;

import com.gmmapowell.script.flow.SectionTitle;
import com.gmmapowell.script.modules.processors.doc.AtCommand;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ArticleCommand implements AtCommandHandler {
	private final ConfiguredState state;

	public ArticleCommand(ScannerAtState sas) {
		this.state = sas.state();
	}
	
	@Override
	public String name() {
		return "Article";
	}

	@Override
	public void invoke(AtCommand cmd) {
		String title = cmd.arg("title");
		if (title == null)
			throw new RuntimeException("Article without title");
		state.newSection("main", "article");
		state.newPara();
		state.newSpan();
		state.op(new SectionTitle(title));
		state.endPara();
	}
}
