package com.gmmapowell.script.modules.processors.presenter;

import org.flasck.flas.blockForm.InputPosition;

public class NumberToken extends Token {
	public final float value;

	public NumberToken(InputPosition loc, float f) {
		super(loc);
		value = f;
	}

}
