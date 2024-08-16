package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.flow.TextSpanItem;

public class StepProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final HorizSpan present;
	private final HorizSpan notes;

	public StepProcessor(ErrorReporter errors, HorizSpan present, HorizSpan notes) {
		this.errors = errors;
		this.present = present;
		this.notes = notes;
	}

	@Override
	public LineProcessor process(ContinuedLine currline) {
		Tokenizable tx = new Tokenizable(currline);
		Token t = Token.from(errors, tx);
		if (t instanceof KeywordToken) {
			String kw = ((KeywordToken)t).kw;
			switch (kw) {
			case "img": {
				Token file = Token.from(errors, tx);
				if (file == null) {
					errors.message(tx, "must specify a file as a string");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (file instanceof StringToken)
					present.items.add(new ImageOp(((StringToken)file).value));
				else
					errors.message(file.location(), "file must be a string");
				return new NoNestingProcessor();
			}
			case "remove": {
				Token file = Token.from(errors, tx);
				if (file == null) {
					errors.message(tx, "must specify a file to remove as a string");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (file instanceof StringToken)
					present.items.add(new ImageOp(((StringToken)file).value));
				else
					errors.message(file.location(), "remove file must be a string");
				return new NoNestingProcessor();
			}
			case "speaker": {
				Token speech = Token.from(errors, tx);
				if (speech == null) {
					errors.message(tx, "must specify a title");
					return new IgnoreNestingProcessor();
				}
				Token.assertEnd(errors, tx);
				if (speech instanceof StringToken)
					notes.items.add(new TextSpanItem(((StringToken)speech).value));
				else
					errors.message(speech.location(), "speech must be a string");
				return new NoNestingProcessor();
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
