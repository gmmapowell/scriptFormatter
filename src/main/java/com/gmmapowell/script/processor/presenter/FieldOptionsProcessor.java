package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.ContinuedLine;
import org.flasck.flas.errors.ErrorReporter;
import org.flasck.flas.tokenizers.Tokenizable;

public class FieldOptionsProcessor implements LineProcessor {
	private final ErrorReporter errors;
	private final String field;

	public FieldOptionsProcessor(ErrorReporter errors, String field) {
		this.errors = errors;
		this.field = field;
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
//		slide.fieldOption(field.location(), this.field, name, sval);
		return new NoNestingProcessor();
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

}
