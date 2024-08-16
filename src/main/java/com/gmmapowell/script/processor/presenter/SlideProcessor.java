package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.blockForm.InputPosition;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.presenter.nodes.Slide;
import com.gmmapowell.script.presenter.nodes.SlideStep;
import com.gmmapowell.script.processor.presenter.slideformats.BoringSlideFormatter;
import com.gmmapowell.script.processor.presenter.slideformats.TitleSlideFormatter;
import com.gmmapowell.script.sink.Sink;

public class SlideProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final Slide slide;
	private final String imagedir;
	private final Sink sink;

	public SlideProcessor(Sink sink, ErrorReporter errors, Slide slide, String imagedir) {
		this.sink = sink;
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
			case "background": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify a color");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof StringToken)
					slide.backgroundColor(((StringToken)name).value);
				else
					errors.message(name.location(), "color must be a string");
				return new NoNestingProcessor();
			}
			case "format": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify a format");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof NameToken) {
					String format = ((NameToken)name).name;
					SlideFormatter sf = findSlideFormatter(name.location(), format);
					if (sf == null)
						return new IgnoreNestingProcessor();
					slide.setFormat(sf);
					return new FormatFieldProcessor(errors, sf, imagedir);
				} else {
					errors.message(name.location(), "format must be a name");
					return new IgnoreNestingProcessor();
				}
			}
			case "img": {
				Token name = Token.from(errors, tx);
				if (name == null) {
					errors.message(tx, "must specify an image");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (name instanceof StringToken)
					slide.backgroundImage(imagedir + ((StringToken)name).value);
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
				errors.message(tx, "invalid keyword for slide: " + kw);
				return new IgnoreNestingProcessor();
			}
		} else {
			errors.message(tx, "expected keyword");
			return new IgnoreNestingProcessor();
		}
	}

	private SlideFormatter findSlideFormatter(InputPosition loc, String format) {
		switch (format) {
		case "title-slide":
			return new TitleSlideFormatter(errors, slide);
		case "boring-slide":
			return new BoringSlideFormatter(errors, slide);
		default:
			errors.message(loc, "there is no formatter for slide " + format);
			return null;
		}
	}

	@Override
	public void flush() {
		Flow flow = new Flow(slide.name(), false);
		sink.flow(flow);
	}

}
