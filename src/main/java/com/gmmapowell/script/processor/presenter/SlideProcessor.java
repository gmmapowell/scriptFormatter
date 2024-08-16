package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.modules.presenter.AspectOp;
import com.gmmapowell.script.modules.presenter.BgColorOp;
import com.gmmapowell.script.modules.presenter.BgImageOp;
import com.gmmapowell.script.modules.presenter.FormatOp;
import com.gmmapowell.script.sink.Sink;

public class SlideProcessor implements LineProcessor, SlideCollector {
	private final Sink sink;
	private final ErrorReporter errors;
	private final String imagedir;
	private final Flow flow;
	private final Section present;
	private final Section notes;
	private final Section meta;

	public SlideProcessor(Sink sink, ErrorReporter errors, String imagedir, Flow flow) {
		this.sink = sink;
		this.errors = errors;
		this.imagedir = imagedir;
		this.flow = flow;
		meta = new Section("meta");
		flow.sections.add(meta);
		present = new Section("present");
		flow.sections.add(present);
		notes = new Section("notes");
		flow.sections.add(notes);
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
				metaOp(new AspectOp(((NumberToken)xt).value, ((NumberToken)yt).value));
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
					metaOp(new BgColorOp(((StringToken)name).value));
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
//					SlideFormatter sf = findSlideFormatter(name.location(), format);
//					if (sf == null)
//						return new IgnoreNestingProcessor();
//					slide.setFormat(sf);
					FormatOp fmt = new FormatOp(format);
					metaOp(fmt);
					return new FormatFieldProcessor(this, errors, imagedir);
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
					metaOp(new BgImageOp(imagedir + ((StringToken)name).value));
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
					text(notes, ((StringToken)name).value);
				else
					errors.message(name.location(), "speaker must speak a string");
				return new NoNestingProcessor();
			}
			case "step": {
				return new StepProcessor(errors, span(present), span(notes));
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

	@Override
	public void metaOp(SpanItem op) {
		Para p = new Para(null);
		HorizSpan span = new HorizSpan(null, null);
		p.spans.add(span);
		span.items.add(op);
		meta.paras.add(p);
	}

	@Override
	public void text(Section s, String tx) {
		Para p = new Para(null);
		HorizSpan span = new HorizSpan(null, null);
		p.spans.add(span);
		span.items.add(new TextSpanItem(tx));
		s.paras.add(p);
	}

	@Override
	public HorizSpan span(Section s) {
		Para p = new Para(null);
		HorizSpan span = new HorizSpan(null, null);
		p.spans.add(span);
		s.paras.add(p);
		return span;
	}

	@Override
	public void flush() {
		sink.flow(flow);
	}

}
