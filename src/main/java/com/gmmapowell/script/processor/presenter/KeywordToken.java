package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.InputPosition;

public class KeywordToken extends Token {
	public final String kw;

	public KeywordToken(InputPosition loc, String kw) {
		super(loc);
		this.kw = kw;
	}

	@Override
	public String toString() {
		return "KW[" + kw + "]";
	}
}
