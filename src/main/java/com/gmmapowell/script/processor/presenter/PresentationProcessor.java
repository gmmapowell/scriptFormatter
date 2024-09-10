package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.modules.processors.presenter.PresenterGlobals;

public class PresentationProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final FlowMap flows;
	private final String imagedir;
	private final PresenterGlobals global;

	public PresentationProcessor(ErrorReporter errors, PresenterGlobals global, FlowMap flows, String imagedir) {
		this.errors = errors;
		this.global = global;
		this.flows = flows;
		this.imagedir = imagedir;
	}

	@Override
	public LineProcessor process(ContinuedLine currline) {
		Tokenizable tx = new Tokenizable(currline);
		Token t = Token.from(errors, tx);
		if (t instanceof KeywordToken) {
			String kw = ((KeywordToken)t).kw;
			switch (kw) {
			case "slide": {
				Token nt = Token.from(errors, tx);
				if (nt == null) {
					errors.message(tx, "need a name for the slide");
					return new IgnoreNestingProcessor();
				}
				if (!(nt instanceof NameToken)) {
					errors.message(nt.location(), "needs to be a name token");
					return new IgnoreNestingProcessor();
				}
				if (tx.hasMore()) {
					errors.message(tx, "end of line expected");
					return new IgnoreNestingProcessor();
				}
				String flowName = ((NameToken)nt).name;
				global.nextSlide(flowName);
				flows.callbackFlow(flowName);
				Flow flow = flows.get(flowName);
				return new SlideProcessor(errors, flow, imagedir);
			}
			default:
				errors.message(tx, "invalid keyword: " + kw);
				return new IgnoreNestingProcessor();
			}
		} else {
			errors.message(tx, "expected keyword");
			return new IgnoreNestingProcessor();
		}
	}

	@Override
	public void flush() {
	}
}
