package com.gmmapowell.script.styles.page;

public class MoviePageStyle extends DefaultPageStyle {

	@Override
	public Float getTopMargin() {
		return 72f;
	}

	@Override
	public Float getBottomMargin() {
		return 88f;
	}
	
	@Override
	public boolean wantFooter() {
		return true;
	}
}
