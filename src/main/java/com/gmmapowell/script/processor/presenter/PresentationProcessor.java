package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.presenter.nodes.Slide;

public class PresentationProcessor implements LineProcessor {
	private final ErrorReporter errors;

	public PresentationProcessor(ErrorReporter errors) {
		this.errors = errors;
	}

	@Override
	public LineProcessor process(ContinuedLine currline) {
		Tokenizable tx = new Tokenizable(currline);
		Token t = Token.from(errors, tx);
		if (t instanceof KeywordToken) {
			String kw = ((KeywordToken)t).kw;
			switch (kw) {
			case "slide": {
				Slide slide = new Slide();
				return new SlideProcessor(errors, slide);
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
		System.out.println("flushing top level handler");
	}

}
