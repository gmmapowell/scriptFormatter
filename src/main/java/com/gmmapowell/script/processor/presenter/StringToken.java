package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.InputPosition;

public class StringToken extends Token {
	public final String value;

	public StringToken(InputPosition loc, String string) {
		super(loc);
		this.value = string;
	}

	@Override
	public String toString() {
		return "Str[" + value + "]";
	}
}
