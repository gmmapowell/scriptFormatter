package com.gmmapowell.script.styles.page;

import com.gmmapowell.script.styles.PageStyle;

public class DefaultPageStyle implements PageStyle {

	@Override
	public Float getPageWidth() {
		return 8.5f * 72;
	}

	@Override
	public Float getPageHeight() {
		return 11f * 72;
	}

	@Override
	public Float getTopMargin() {
		return 72f;
	}

	@Override
	public Float getBottomMargin() {
		return 72f;
	}

	@Override
	public Float getLeftMargin() {
		return 72f;
	}

	@Override
	public Float getRightMargin() {
		return 72f;
	}
}
