package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.presenter.nodes.SlideStep;

public class SlideProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final Slide slide;
	private final String imagedir;

	public SlideProcessor(ErrorReporter errors, Slide slide, String imagedir) {
		this.errors = errors;
		this.slide = slide;
		this.imagedir = imagedir;
	}

	@Override
	public LineProcessor process(ContinuedLine currline) {
		Tokenizable tx = new Tokenizable(currline);
		Token t = Token.from(errors, tx);
		if (t instanceof KeywordToken) {
			String kw = ((KeywordToken)t).kw;
			switch (kw) {
			case "aspect": {
				Token xt = Token.from(errors, tx);
				if (xt == null) {
					errors.message(tx, "must specify an aspect x");
					return new IgnoreNestingProcessor();
				}
				if (!(xt instanceof NumberToken)) {
					errors.message(tx, "aspect x must be a number");
					return new IgnoreNestingProcessor();
				}
				Token yt = Token.from(errors, tx);
				if (yt == null) {
					errors.message(tx, "must specify an aspect y");
					return new IgnoreNestingProcessor();
				}
				if (!(yt instanceof NumberToken)) {
					errors.message(tx, "aspect y must be a number");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				slide.aspect(((NumberToken)xt).value, ((NumberToken)yt).value);
				return new NoNestingProcessor();
			}
			case "format": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify a format");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof NameToken)
					slide.setFormat(((NameToken)name).name);
				else
					errors.message(name.location(), "format must be a name");
				return new FormatFieldProcessor(errors, slide);
			}
			case "title": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify a title");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof StringToken)
					slide.setTitle(((StringToken)name).value);
				else
					errors.message(name.location(), "title must be a string");
				return new NoNestingProcessor();
			}
			case "img": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify an image");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof StringToken)
					slide.background(imagedir + ((StringToken)name).value);
				else
					errors.message(name.location(), "image name must speak a string");
				return new NoNestingProcessor();
			}
			case "speaker": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify a title");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof StringToken)
					slide.speak(((StringToken)name).value);
				else
					errors.message(name.location(), "speaker must speak a string");
				return new NoNestingProcessor();
			}
			case "step": {
				SlideStep step = new SlideStep();
				slide.addStep(step);
				return new StepProcessor(errors, step);
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
		// TODO Auto-generated method stub

	}

}
