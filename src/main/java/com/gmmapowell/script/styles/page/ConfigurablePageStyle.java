package com.gmmapowell.script.styles.page;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.styles.PageStyle;

public class ConfigurablePageStyle implements PageStyle {
	private Float pageWidth;
	private Float pageHeight;
	private Float topMargin;
	private Float bottomMargin;
	private Float leftMargin;
	private Float rightMargin;
	private Boolean wantPageNumbers;
	private Float pageNumberCenterX;
	private Float pageNumberBaselineY;
	private PDFont pageNumberFont;
	private Float pageNumberFontSize;
	private Boolean wantHeader;
	private Boolean wantFooter;

	@Override
	public Float getPageWidth() {
		return pageWidth;
	}

	@Override
	public Float getPageHeight() {
		return pageHeight;
	}

	@Override
	public Float getTopMargin() {
		return topMargin;
	}

	@Override
	public Float getBottomMargin() {
		return bottomMargin;
	}

	@Override
	public Float getLeftMargin() {
		return leftMargin;
	}

	@Override
	public Float getRightMargin() {
		return rightMargin;
	}

	@Override
	public Boolean wantPageNumbers() {
		return wantPageNumbers;
	}

	@Override
	public Float pageNumberCenterX() {
		return pageNumberCenterX;
	}

	@Override
	public Float pageNumberBaselineY() {
		return pageNumberBaselineY;
	}

	@Override
	public PDFont getPageNumberFont() {
		return pageNumberFont;
	}

	@Override
	public Float getPageNumberFontSize() {
		return pageNumberFontSize;
	}

	@Override
	public Boolean wantHeader() {
		return wantHeader;
	}

	@Override
	public Boolean wantFooter() {
		return wantFooter;
	}

	public void setPageWidth(Float pageWidth) {
		this.pageWidth = pageWidth;
	}

	public void setPageHeight(Float pageHeight) {
		this.pageHeight = pageHeight;
	}

	public void setTopMargin(Float topMargin) {
		this.topMargin = topMargin;
	}

	public void setBottomMargin(Float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public void setLeftMargin(Float leftMargin) {
		this.leftMargin = leftMargin;
	}

	public void setRightMargin(Float rightMargin) {
		this.rightMargin = rightMargin;
	}

	public void setWantPageNumbers(boolean wantPageNumbers) {
		this.wantPageNumbers = wantPageNumbers;
	}

	public void setPageNumberCenterX(float pageNumberCenterX) {
		this.pageNumberCenterX = pageNumberCenterX;
	}

	public void setPageNumberBaselineY(float pageNumberBaselineY) {
		this.pageNumberBaselineY = pageNumberBaselineY;
	}

	public void setPageNumberFont(PDFont pageNumberFont) {
		this.pageNumberFont = pageNumberFont;
	}

	public void setPageNumberFontSize(float pageNumberFontSize) {
		this.pageNumberFontSize = pageNumberFontSize;
	}

	public void setWantHeader(boolean wantHeader) {
		this.wantHeader = wantHeader;
	}

	public void setWantFooter(boolean wantFooter) {
		this.wantFooter = wantFooter;
	}

	public ConfigurablePageStyle applyAll(PageStyle from) {
		if (from.getPageWidth() != null)
			this.setPageWidth(from.getPageWidth());
		if (from.getPageHeight() != null)
			this.setPageHeight(from.getPageHeight());
		if (from.getTopMargin() != null)
			this.setTopMargin(from.getTopMargin());
		if (from.getBottomMargin() != null)
			this.setBottomMargin(from.getBottomMargin());
		if (from.getLeftMargin() != null)
			this.setLeftMargin(from.getLeftMargin());
		if (from.getRightMargin() != null)
			this.setRightMargin(from.getRightMargin());
		if (from.wantPageNumbers() != null)
			this.setWantPageNumbers(from.wantPageNumbers());
		if (from.pageNumberCenterX() != null)
			this.setPageNumberCenterX(from.pageNumberCenterX());
		if (from.pageNumberBaselineY() != null)
			this.setPageNumberBaselineY(from.pageNumberBaselineY());
		if (from.getPageNumberFont() != null)
			this.setPageNumberFont(from.getPageNumberFont());
		if (from.getPageNumberFontSize() != null)
			this.setPageNumberFontSize(from.getPageNumberFontSize());
		if (from.wantHeader() != null)
			this.setWantHeader(from.wantHeader());
		if (from.wantFooter() != null)
			this.setWantFooter(from.wantFooter());
		return this;
	}
}
