package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.presenter.nodes.Presentation;
import com.gmmapowell.script.presenter.nodes.Slide;

public class PresentationProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final PresentationMapper mapper;
	private final Presentation presentation;
	private final String imagedir;

	public PresentationProcessor(ErrorReporter errors, PresentationMapper mapper, String imagedir, String name) {
		this.errors = errors;
		this.mapper = mapper;
		this.imagedir = imagedir;
		this.presentation = new Presentation(name);
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
				Slide slide = new Slide(((NameToken)nt).name);
				presentation.add(slide);
				return new SlideProcessor(errors, slide, imagedir);
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
		mapper.present(presentation);
	}
}
