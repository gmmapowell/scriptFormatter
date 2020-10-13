package com.gmmapowell.script.styles.page;

public class BookPageStyle extends DefaultPageStyle {

	@Override
	public Float getPageWidth() {
		return 5.5f * 72;
	}

	@Override
	public Float getPageHeight() {
		return 8.5f * 72;
	}

	@Override
	public Float getTopMargin() {
		return 36f;
	}

	@Override
	public Float getBottomMargin() {
		return 36f;
	}
}
