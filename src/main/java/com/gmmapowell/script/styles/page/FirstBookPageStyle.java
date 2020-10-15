package com.gmmapowell.script.styles.page;

public class FirstBookPageStyle extends RightBookPageStyle {

	@Override
	public Float getTopMargin() {
		return 132f;
	}

	@Override
	public boolean wantHeader() {
		return false;
	}

}
