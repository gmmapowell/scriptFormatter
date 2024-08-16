package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;

public class PresentationProcessor implements LineProcessor {
	private final Sink sink;
	private final ErrorReporter errors;
	private final String imagedir;

	public PresentationProcessor(Sink sink, ErrorReporter errors, String imagedir) {
		this.sink = sink;
		this.errors = errors;
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
				Flow flow = new Flow(((NameToken)nt).name, false);
				return new SlideProcessor(sink, errors, imagedir, flow);
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
