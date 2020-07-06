package com.gmmapowell.script.processor.presenter;

import org.flasck.flas.blockForm.InputPosition;

public class NameToken extends Token {
	public final String name;

	public NameToken(InputPosition loc, String kw) {
		super(loc);
		this.name = kw;
	}

	@Override
	public String toString() {
		return "Name[" + name + "]";
	}
}
