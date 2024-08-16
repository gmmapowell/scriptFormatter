package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

import com.gmmapowell.script.modules.presenter.FieldOp;

public class FormatFieldProcessor implements LineProcessor {
	private final SlideCollector collector;
	private final ErrorReporter errors;
	private final String imagedir;

	public FormatFieldProcessor(SlideCollector collector, ErrorReporter errors, String imagedir) {
		this.collector = collector;
		this.errors = errors;
		this.imagedir = imagedir;
	}

	@Override
	public LineProcessor process(ContinuedLine currline) {
		Tokenizable line = new Tokenizable(currline);
		Token field = Token.from(errors, line);
		if (field == null) {
			errors.message(line, "need field name");
			return new IgnoreNestingProcessor();
		}
		if (!(field instanceof NameToken)) {
			errors.message(line, "field name must be name");
			return new IgnoreNestingProcessor();
		}
		Token op = Token.from(errors, line);
		if (op == null) {
			errors.message(line, "need <- op");
			return new IgnoreNestingProcessor();
		}
		if (!(op instanceof OpToken) || !((OpToken)op).op.equals("<-")) {
			errors.message(op.location(), "need <- op");
			return new IgnoreNestingProcessor();
		}
		Token val = Token.from(errors, line);
		if (val == null) {
			errors.message(line, "need value");
			return new IgnoreNestingProcessor();
		}
		if (!(val instanceof StringToken)) {
			errors.message(line, "expected string value");
			return new IgnoreNestingProcessor();
		}
		String name = ((NameToken)field).name;
		String sval = ((StringToken)val).value;
		if (name.equals("image"))
			sval = imagedir + sval;
		collector.metaOp(new FieldOp(name, sval));
		return new FieldOptionsProcessor(collector, errors, name);
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}
