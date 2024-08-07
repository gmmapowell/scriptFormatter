package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.SectionTitle;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.sink.Sink;

public class ArticlePipeline extends AtPipeline<ArticleState> {
	private ArticleState state;

	public ArticlePipeline(Region root, ElementFactory ef, Sink sink, VarMap options, boolean debug) throws ConfigException {
		super(root, ef, sink, options, debug);
	}

	@Override
	protected ArticleState begin(Map<String, Flow> flows, String file) {
		state = new ArticleState(flows, file);
		state.flows.put("main", new Flow("main", true));
		return state;
	}

	@Override
	protected void handleLine(ArticleState state, String s) throws IOException {
		if (super.commonHandleLine(state, s))
			return;
		if (!state.inPara()) {
			if (state.blockquote)
				state.newPara("blockquote");
			else
				state.newPara("text");
		} else if (joinspace) {
			if (!state.inSpan())
				state.newSpan();
			state.op(new BreakingSpace());
		}
		ProcessingUtils.process(state, s);
	}

	@Override
	protected void commitCurrentCommand() throws IOException {
		if (state.cmd != null) {
			switch(state.cmd.name) {
			case "Article": {
				String title = state.cmd.args.get("title");
				if (title == null)
					throw new RuntimeException("Article without title");
				state.newSection("main", "article");
				Para np = new Para(new ArrayList<>());
				HorizSpan hz = new HorizSpan(null, new ArrayList<>());
				np.spans.add(hz);
				hz.items.add(new SectionTitle(title));
				state.currSection.paras.add(np);
				break;
			}
			default:
				throw new NotImplementedException("commit: " + state.cmd.name);
			}
			state.cmd = null;
		} else if (state.inline != null) {
			throw new NotImplementedException();
		} else if (state.inPara()) {
			state.endPara();
		}
	}
}
