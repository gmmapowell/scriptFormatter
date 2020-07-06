package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.InputPosition;

public class OpToken extends Token {
	public final String op;

	public OpToken(InputPosition loc, String kw) {
		super(loc);
		this.op = kw;
	}

	@Override
	public String toString() {
		return "Op[" + op + "]";
	}
}
