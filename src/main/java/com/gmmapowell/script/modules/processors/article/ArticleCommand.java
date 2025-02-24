package com.gmmapowell.script.modules.processors.article;

import com.gmmapowell.script.flow.SaveAs;
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
		String saveAs = cmd.arg("saveAs");
		if (saveAs == null)
			saveAs = title;
		state.ensureFlow(title);
		state.newSection(title, "article");
		state.newPara();
		state.newSpan();
		state.op(new SaveAs(saveAs));
		state.endPara();
	}
}
